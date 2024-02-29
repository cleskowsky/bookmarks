package net.leskowsky.bookmarks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookmarksControllerTests {

    // get bookmarks
    @Test
    public void getBookmarks(@Autowired WebTestClient client) {
        client.get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(v -> {
                    assertTrue(v.contains("<button type=\"submit\">Add</button>"));
                });
    }

    // add a bookmark
}
