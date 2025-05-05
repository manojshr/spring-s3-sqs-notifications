package io.codlibs.aws.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringS3SqsNotificationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringS3SqsNotificationsApplication.class, args);
	}

}
