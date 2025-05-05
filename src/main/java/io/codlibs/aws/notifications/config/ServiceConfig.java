package io.codlibs.aws.notifications.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codlibs.aws.notifications.repository.UserProfileRepository;
import io.codlibs.aws.notifications.repository.UserTodoRepository;
import io.codlibs.aws.notifications.scheduled.UserDataAggregationService;
import io.codlibs.aws.notifications.service.S3Service;
import io.codlibs.aws.notifications.service.UserProfileService;
import io.codlibs.aws.notifications.service.UserService;
import io.codlibs.aws.notifications.service.UserTodosService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Configuration
public class ServiceConfig {

    @Bean
    public S3Service s3Service(S3AsyncClient s3AsyncClient, ObjectMapper objectMapper,
                               @Value("${spring.cloud.aws.s3.bucket}") String bucket) {
        return new S3Service(s3AsyncClient, objectMapper, bucket);
    }

    @Bean
    public UserProfileRepository userProfileRepository() {
        return new UserProfileRepository();
    }

    @Bean
    public UserTodoRepository userTodoRepository() {
        return new UserTodoRepository();
    }

    @Bean
    public UserService userProfileService(S3Service s3Service, UserProfileRepository userProfileRepository) {
        return new UserProfileService(s3Service, userProfileRepository);
    }

    @Bean
    public UserService userTodosService(S3Service s3Service, UserTodoRepository userTodoRepository) {
        return new UserTodosService(s3Service, userTodoRepository);
    }

    @Bean
    public UserDataAggregationService userDataAggregationService(UserProfileRepository userProfileRepository,
                                                                 UserTodoRepository userTodoRepository) {
        return new UserDataAggregationService(userProfileRepository, userTodoRepository);
    }
}
