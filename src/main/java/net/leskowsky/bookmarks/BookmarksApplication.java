package net.leskowsky.bookmarks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class BookmarksApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookmarksApplication.class, args);
	}
}
