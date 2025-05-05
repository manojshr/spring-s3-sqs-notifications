package io.codlibs.aws.notifications.helper;

import com.github.javafaker.Faker;
import io.codlibs.aws.notifications.model.UserProfile;
import io.codlibs.aws.notifications.model.UserTodo;

import java.util.UUID;
import java.util.stream.IntStream;

public class DataGenerator {

    public static UserProfile generateUserProfile() {
        Faker faker = new Faker();
        String gender = faker.random().nextBoolean() ? "MALE" : "FEMALE";
        return new UserProfile(
                UUID.randomUUID().toString(),
                faker.name().username(),
                faker.internet().emailAddress(),
                faker.name().firstName(),
                faker.name().lastName(),
                faker.random().nextInt(15,50),
                gender,
                true
        );
    }

    public static UserTodo generateUserTodo(String userId) {
        Faker faker = new Faker();
        int todoSize = faker.random().nextInt(1, 10);
        var todos = IntStream.rangeClosed(1, todoSize)
                .mapToObj(i -> new UserTodo.Todo(faker.lorem().sentence(3), faker.lorem().paragraph(3)))
                .toList();
        return new UserTodo(
                userId,
                todos
        );
    }
}
