package io.codlibs.aws.notifications.repository;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class UserRepository<Id, Type> {

    private final Map<Id, Type> datastore = new HashMap<>();

    protected Mono<Type> save(Id id, Type value) {
        return Optional.ofNullable(datastore.put(id, value))
                .map(Mono::just)
                .orElseGet(Mono::empty);
    }

    protected Optional<Type> findById(Id id) {
        return Optional.ofNullable(datastore.get(id));
    }

    protected Map<Id, Type> getAll() {
        return new HashMap<>(datastore);
    }
}
