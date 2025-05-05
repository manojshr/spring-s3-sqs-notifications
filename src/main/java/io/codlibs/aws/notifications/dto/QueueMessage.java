package io.codlibs.aws.notifications.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QueueMessage(@JsonProperty("Records") List<Record> records) {
    public record Record(@JsonProperty("s3") S3Metadata s3Metadata) {
    }

    public record S3Metadata(@JsonProperty("object") S3Object s3Object) {
    }

    public record S3Object(@JsonProperty("key") String key) {
    }
}
