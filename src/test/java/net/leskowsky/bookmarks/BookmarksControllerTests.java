package net.leskowsky.bookmarks;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringJUnitConfig
public class BookmarksControllerTests {

    WebTestClient client;

    @Test
    public void index(ApplicationContext context) {
        client = WebTestClient.bindToApplicationContext(context).build();
        client.get().uri("/")
                .exchange()
                .expectStatus().isOk();
    }
}
