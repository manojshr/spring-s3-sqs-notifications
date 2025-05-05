package io.codlibs.aws.notifications.repository;

import io.codlibs.aws.notifications.model.UserProfile;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

public class UserProfileRepository extends UserRepository<String, UserProfile> {

    public Mono<UserProfile> save(UserProfile profile) {
        return super.save(profile.id(), profile);
    }

    public Optional<UserProfile> findById(String id) {
        return super.findById(id);
    }

    @Override
    public Map<String, UserProfile> getAll() {
        return super.getAll();
    }
}
