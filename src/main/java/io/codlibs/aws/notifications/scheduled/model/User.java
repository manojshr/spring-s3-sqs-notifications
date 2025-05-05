package io.codlibs.aws.notifications.scheduled.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.codlibs.aws.notifications.model.UserProfile;
import io.codlibs.aws.notifications.model.UserTodo;

import java.util.List;

public record User(@JsonProperty("profile")
                   UserProfile userProfile,
                   @JsonIgnoreProperties(value = "userId")
                   @JsonProperty("todos")
                   List<UserTodo.Todo> todos) {
}
