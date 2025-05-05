package io.codlibs.aws.notifications.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codlibs.aws.notifications.dto.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class S3Service {

    private final S3AsyncClient s3AsyncClient;

    private final ObjectMapper objectMapper;

    private final String bucket;

    private final Logger logger = LoggerFactory.getLogger(S3Service.class);

    public S3Service(S3AsyncClient s3AsyncClient, ObjectMapper objectMapper, String bucket) {
        this.s3AsyncClient = s3AsyncClient;
        this.objectMapper = objectMapper;
        this.bucket = bucket;
    }

    /**
     * Reads a file from S3 and maps it to the specified class.
     *
     * @param s3Metadata Metadata of the S3 object to read.
     * @param tClass     Class to map the S3 object to.
     * @param <T>        Type of the class to map to.
     * @return A Mono containing the mapped object.
     */
    public <T> Mono<T> readS3File(QueueMessage.S3Metadata s3Metadata, Class<T> tClass) {
        return Mono.fromFuture(() -> s3AsyncClient.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(URLDecoder.decode(s3Metadata.s3Object().key(), StandardCharsets.UTF_8))
                .build(), AsyncResponseTransformer.toBytes()))
                .map(bytes -> {
                    try {
                        return objectMapper.readValue(bytes.asByteArray(), tClass);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to parse S3 file: %s".formatted(tClass.getSimpleName()), e);
                    }
                })
                .doOnSuccess(res -> logger.info("Successfully read {} from S3", tClass.getSimpleName()))
                .doOnError(th -> logger.error("Failed to read {} from S3", tClass.getSimpleName(), th));
    }
}
