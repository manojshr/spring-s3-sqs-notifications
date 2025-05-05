package io.codlibs.aws.notifications.service;

import io.codlibs.aws.notifications.dto.QueueMessage;
import io.codlibs.aws.notifications.model.UserProfile;
import io.codlibs.aws.notifications.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public class UserProfileService implements UserService {

    @Autowired
    private final S3Service s3Service;

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(S3Service s3Service, UserProfileRepository userProfileRepository) {
        this.s3Service = s3Service;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public Mono<Boolean> processMessage(QueueMessage.Record queueMessageRecord) {
        return s3Service.readS3File(queueMessageRecord.s3Metadata(), UserProfile.class)
                .flatMap(userProfileRepository::save)
                .thenReturn(true);
    }
}
