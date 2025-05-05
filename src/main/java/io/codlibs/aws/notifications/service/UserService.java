package io.codlibs.aws.notifications.service;

import io.codlibs.aws.notifications.dto.QueueMessage;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<Boolean> processMessage(QueueMessage.Record sqsMessageRecord);
}
