package io.codlibs.aws.notifications.ext;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

public class LocalStackTestContainerExtension implements BeforeAllCallback {

    public static final LocalStackContainer localStackContainer = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:4.3"))
            .withEnv("DEFAULT_REGION", "us-east-1")
            .withServices(S3, SQS)
            .withReuse(true);

    @DynamicPropertySource
    public static void bindProps(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.s3.endpoint", () -> localStackContainer.getEndpointOverride(S3));
        registry.add("spring.cloud.aws.sqs.endpoint", () -> localStackContainer.getEndpointOverride(SQS));
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!localStackContainer.isRunning()) {
            localStackContainer.start();
        }
    }
}
