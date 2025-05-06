package io.codlibs.aws.notifications.events;

import io.codlibs.aws.notifications.base.LocalStackSqsS3TestBase;
import io.codlibs.aws.notifications.dto.QueueMessage;
import io.codlibs.aws.notifications.service.UserProfileService;
import io.codlibs.aws.notifications.service.UserTodosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserSqsListenerTest extends LocalStackSqsS3TestBase {

    @Autowired
    private S3AsyncClient s3AsyncClient;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("classpath:user-profile.json")
    private Resource userProfileResource;

    @Value("classpath:user-todo.json")
    private Resource userTodoResource;

    @SpyBean
    private UserProfileService userProfileService;

    @SpyBean
    private UserTodosService userTodosService;

    @Test
    void verifyUserProfile() throws IOException {
        uploadToS3("user-profile/12345.json", userProfileResource.getContentAsString(UTF_8));
        uploadToS3("user-todo/12345.json", userTodoResource.getContentAsString(UTF_8));

        await()
                .atMost(10, SECONDS)
                .pollDelay(500, MILLISECONDS)
                .untilAsserted(() -> {
                    verify(userProfileService, times(1)).processMessage(any(QueueMessage.Record.class));
                    verify(userTodosService, times(1)).processMessage(any(QueueMessage.Record.class));
                });
    }

    void uploadToS3(String key, String content) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3AsyncClient.putObject(request, AsyncRequestBody.fromString(content))
                .join();
    }
}
