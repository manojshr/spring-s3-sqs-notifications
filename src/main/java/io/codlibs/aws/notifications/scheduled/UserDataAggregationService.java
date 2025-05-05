package io.codlibs.aws.notifications.scheduled;

import io.codlibs.aws.notifications.repository.UserProfileRepository;
import io.codlibs.aws.notifications.repository.UserTodoRepository;
import io.codlibs.aws.notifications.scheduled.model.User;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class UserDataAggregationService {

    private final UserProfileRepository userProfileRepository;

    private final UserTodoRepository userTodoRepository;

    public UserDataAggregationService(UserProfileRepository userProfileRepository, UserTodoRepository userTodoRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userTodoRepository = userTodoRepository;
    }

    public List<User> getAllUsers() {
        var userProfilesMap = userProfileRepository.getAll();
        var userTodosMap = userTodoRepository.getAll();

        return userProfilesMap.entrySet().stream()
                .map(entry -> {
                    var todo = userTodosMap.get(entry.getKey());
                    return nonNull(todo) ? new User(entry.getValue(), todo.todos()) : null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
