package io.codlibs.aws.notifications.base;

import io.codlibs.aws.notifications.ext.LocalStackTestContainerExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.io.IOException;

import static io.codlibs.aws.notifications.ext.LocalStackTestContainerExtension.localStackContainer;

@SpringBootTest
@ExtendWith(LocalStackTestContainerExtension.class)
public class LocalStackSqsS3TestBase {

    private final static String s3BucketNotificationConfig = """
            {
                "QueueConfigurations": [
                  {
                    "QueueArn": "arn:aws:sqs:%s:000000000000:%s",
                    "Events": ["s3:ObjectCreated:*"],
                    "Filter": {
                      "Key": {
                        "FilterRules": [
                          {
                            "Name": "prefix",
                            "Value": "user-%s/"
                          }
                        ]
                      }
                    }
                  }
                ]
            }""";

    private final static String s3SqsSendMessageConfig = """
            {
              "Policy": "{\\"Version\\":\\"2012-10-17\\",\\"Statement\\":[{\\"Effect\\":\\"Allow\\",\\"Principal\\":\\"*\\",\\"Action\\":\\"sqs:SendMessage\\",\\"Resource\\":\\"arn:aws:sqs:%s:000000000000:%s\\",\\"Condition\\":{\\"ArnLike\\":{\\"aws:SourceArn\\":\\"arn:aws:s3:::%s\\"}}}]}"
            }""";

    @BeforeAll
    public static void beforeAll(@Value("${spring.cloud.aws.region.static}") String region,
                                 @Value("${spring.cloud.aws.s3.bucket}") String bucket,
                                 @Value("${queue.profile.name}") String profileQueueName,
                                 @Value("${queue.todo.name}") String todoQueueName,
                                 @Autowired SqsAsyncClient sqsAsyncClient) throws IOException, InterruptedException {
        localStackContainer.execInContainer(
                "awslocal", "s3api", "create-bucket", "--bucket", bucket
        );
        localStackContainer.execInContainer(
                "awslocal", "sqs", "create-queue", "--queue-name", profileQueueName
        );
        localStackContainer.execInContainer(
                "awslocal", "sqs", "create-queue", "--queue-name", todoQueueName
        );
        var profileQueueUrl = sqsAsyncClient.getQueueUrl(req -> req.queueName(profileQueueName)).join().queueUrl();
        var todoQueueUrl = sqsAsyncClient.getQueueUrl(req -> req.queueName(todoQueueName)).join().queueUrl();

        localStackContainer.execInContainer(
                "awslocal", "s3api", "put-bucket-notification-configuration",
                "--bucket", bucket,
                "--notification-configuration", s3BucketNotificationConfig.formatted(region, profileQueueName, "profile")
        );
        localStackContainer.execInContainer(
                "awslocal", "sqs", "set-queue-attributes", "--queue-url", profileQueueUrl,
                "--attributes", s3SqsSendMessageConfig.formatted(region, profileQueueName, bucket)
        );

        localStackContainer.execInContainer(
                "awslocal", "s3api", "put-bucket-notification-configuration",
                "--bucket", bucket,
                "--notification-configuration", s3BucketNotificationConfig.formatted(region, todoQueueName, "todo")
        );
        localStackContainer.execInContainer(
                "awslocal", "sqs", "set-queue-attributes", "--queue-url", todoQueueUrl,
                "--attributes", s3SqsSendMessageConfig.formatted(region, todoQueueName, bucket)
        );
    }
}
