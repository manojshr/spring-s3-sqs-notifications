package io.codlibs.aws.notifications.repository;

import io.codlibs.aws.notifications.model.UserTodo;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

public class UserTodoRepository extends UserRepository<String, UserTodo> {

    public Mono<UserTodo> save(UserTodo value) {
        return super.save(value.userId(), value);
    }

    public Optional<UserTodo> findById(String id) {
        return super.findById(id);
    }

    @Override
    public Map<String, UserTodo> getAll() {
        return super.getAll();
    }
}
