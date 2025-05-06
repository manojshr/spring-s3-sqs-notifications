package io.codlibs.aws.notifications.service;

import io.codlibs.aws.notifications.dto.QueueMessage;
import io.codlibs.aws.notifications.model.UserProfile;
import io.codlibs.aws.notifications.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class UserProfileService implements UserService {

    private final S3Service s3Service;

    private final UserProfileRepository userProfileRepository;

    private final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    public UserProfileService(S3Service s3Service, UserProfileRepository userProfileRepository) {
        this.s3Service = s3Service;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public Mono<Boolean> processMessage(QueueMessage.Record queueMessageRecord) {
        logger.info("Processing user.profile message");
        return s3Service.readS3File(queueMessageRecord.s3Metadata(), UserProfile.class)
                .flatMap(userProfileRepository::save)
                .thenReturn(true);
    }
}
