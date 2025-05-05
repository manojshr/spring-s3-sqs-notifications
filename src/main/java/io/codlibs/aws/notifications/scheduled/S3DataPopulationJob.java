package io.codlibs.aws.notifications.scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.codlibs.aws.notifications.helper.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(name = "data-population.user.s3.enabled", havingValue = "true")
public class S3DataPopulationJob {

    private final UserDataAggregationService userDataAggregationService;
    private final S3AsyncClient s3AsyncClient;
    private final ObjectMapper objectMapper;
    private final String bucket;

    private final Logger logger = LoggerFactory.getLogger(S3DataPopulationJob.class);

    public S3DataPopulationJob(UserDataAggregationService userDataAggregationService, S3AsyncClient s3AsyncClient,
                               ObjectMapper objectMapper,
                               @Value("${spring.cloud.aws.s3.bucket}") String bucket) {
        this.userDataAggregationService = userDataAggregationService;
        this.s3AsyncClient = s3AsyncClient;
        this.objectMapper = objectMapper;
        this.bucket = bucket;
    }

    @Scheduled(
            fixedDelay = 30,
            initialDelay = 1,
            timeUnit = TimeUnit.SECONDS
    )
    public void populateUserProfileAndUserTodoInS3() throws JsonProcessingException, InterruptedException {
        logger.info("Populating user profile and user todo in S3");
        var userProfile = DataGenerator.generateUserProfile();
        byte[] userProfileDataBytes = objectMapper.writeValueAsBytes(userProfile);
        var userProfileKey = "user-profile/%s.json".formatted(userProfile.id());
        var userProfileRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(userProfileKey)
                .build();
        s3AsyncClient.putObject(userProfileRequest, AsyncRequestBody.fromBytes(userProfileDataBytes))
                .join();
        logger.info("User profile data populated in S3: {}", userProfileKey);

        Thread.sleep(1000); //Wait for 1 second

        var userTodo = DataGenerator.generateUserTodo(userProfile.id());
        byte[] userTodoDataBytes = objectMapper.writeValueAsBytes(userTodo);
        var userTodoKey = "user-todo/%s.json".formatted(userTodo.userId());
        var userTodoRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(userTodoKey)
                .build();
        s3AsyncClient.putObject(userTodoRequest, AsyncRequestBody.fromBytes(userTodoDataBytes))
                .join();
        logger.info("User todo data populated in S3: {}", userTodoKey);
    }

    @Scheduled(
            fixedDelay = 10,
            initialDelay = 30,
            timeUnit = TimeUnit.SECONDS
    )
    public void printer() throws JsonProcessingException {
        logger.info("Logging Users: {}", objectMapper.writeValueAsString(userDataAggregationService.getAllUsers()));
    }
}
