package io.codlibs.aws.notifications.service;

import io.codlibs.aws.notifications.dto.QueueMessage;
import io.codlibs.aws.notifications.model.UserTodo;
import io.codlibs.aws.notifications.repository.UserTodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class UserTodosService implements UserService {

    private final S3Service s3Service;

    private final UserTodoRepository userTodoRepository;

    private final Logger logger = LoggerFactory.getLogger(UserTodosService.class);

    public UserTodosService(S3Service s3Service, UserTodoRepository userTodoRepository) {
        this.s3Service = s3Service;
        this.userTodoRepository = userTodoRepository;
    }

    @Override
    public Mono<Boolean> processMessage(QueueMessage.Record queueMessageRecord) {
        logger.info("Processing user.todo message");
        return s3Service.readS3File(queueMessageRecord.s3Metadata(), UserTodo.class)
                .flatMap(userTodoRepository::save)
                .thenReturn(true);
    }
}
