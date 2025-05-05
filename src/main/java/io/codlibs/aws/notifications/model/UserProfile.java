package io.codlibs.aws.notifications.model;

public record UserProfile(String id,
                          String username,
                          String email,
                          String firstName,
                          String lastName,
                          int age,
                          String gender,
                          boolean active) {
}
