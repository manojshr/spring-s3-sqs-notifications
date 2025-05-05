package io.codlibs.aws.notifications.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Bean
    public S3AsyncClient s3AsyncClient(@Value("${spring.cloud.aws.s3.endpoint}") String s3Endpoint,
                                       @Value("${spring.cloud.aws.region.static}") String region,
                                       @Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
                                       @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey) {
        return S3AsyncClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(s3Endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
