package io.codlibs.aws.notifications.model;

import java.util.List;

public record UserTodo(String userId,
                       List<Todo> todos) {
    public record Todo(String title,
                       String description) {
    }
}
