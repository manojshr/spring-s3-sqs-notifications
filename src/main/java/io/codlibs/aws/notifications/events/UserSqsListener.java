package io.codlibs.aws.notifications.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import io.codlibs.aws.notifications.dto.QueueMessage;
import io.codlibs.aws.notifications.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Service
public class UserSqsListener {

    private final ObjectMapper objectMapper;

    private final UserService userProfileService;

    private final UserService userTodosService;

    private final Logger logger = LoggerFactory.getLogger(UserSqsListener.class);

    public UserSqsListener(ObjectMapper objectMapper,
                           UserService userProfileService,
                           UserService userTodosService) {
        this.objectMapper = objectMapper;
        this.userProfileService = userProfileService;
        this.userTodosService = userTodosService;
    }

    @SqsListener(
            value = "${queue.todo.name}",
            messageVisibilitySeconds = "10",
            maxMessagesPerPoll = "1",
            maxConcurrentMessages = "1",
            acknowledgementMode = "MANUAL"
    )
    public void userTodosListener(Message message, Acknowledgement acknowledgement) {
        logger.info("Received user.todo SQS message: {}", message.body());
        readSqsMessageRecords(message.body())
                .ifPresentOrElse(records -> Flux.fromIterable(records)
                        .flatMap(sqsMessageRecord -> userTodosService.processMessage(sqsMessageRecord)
                                .doOnError(th -> logger.error("Failed to process user.todo SQS record", th))
                                .onErrorResume(th -> Mono.just(false)))
                        .doOnNext(res -> {
                            logger.info("Processed user.todo Record: {}", res);
                        })
                        .doOnComplete(() -> {
                            logger.info("Successfully processed user.todo SQS message");
                            acknowledgement.acknowledge();
                        })
                        .subscribe(), () -> {
                    logger.error("Failed to read user.todo SQS message");
                    acknowledgement.acknowledge();
                });

    }

    @SqsListener(
            value = "${queue.profile.name}",
            messageVisibilitySeconds = "10",
            maxMessagesPerPoll = "1",
            maxConcurrentMessages = "1",
            acknowledgementMode = "MANUAL"
    )
    public void userProfileListener(Message message, Acknowledgement acknowledgement) {
        logger.info("Received user.profile SQS message: {}", message.body());
        readSqsMessageRecords(message.body())
                .ifPresentOrElse(records -> Flux.fromIterable(records)
                        .flatMap(sqsMessageRecord -> userProfileService.processMessage(sqsMessageRecord)
                                .doOnError(th -> logger.error("Failed to process user.profile SQS record", th))
                                .onErrorResume(th -> Mono.just(false)))
                        .doOnNext(res -> {
                            logger.info("Processed user.profile Record: {}", res);
                        })
                        .doOnComplete(() -> {
                            logger.info("Successfully processed user.profile SQS message");
                            acknowledgement.acknowledge();
                        })
                        .subscribe(), () -> {
                    logger.error("Failed to read user.profile SQS message");
                    acknowledgement.acknowledge();
                });
    }

    private Optional<List<QueueMessage.Record>> readSqsMessageRecords(String messageBody) {
        return Optional.ofNullable(messageBody)
                .map(s -> {
                    try {
                        return objectMapper.readValue(s, QueueMessage.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .map(QueueMessage::records)
                .filter(not(CollectionUtils::isEmpty));
    }
}
