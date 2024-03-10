package net.leskowsky.bookmarks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookmarksControllerTests {

    @Autowired
    WebApplicationContext wac;

    private WebTestClient client;

    @BeforeEach
    public void setup() {
        client = MockMvcWebTestClient.bindToApplicationContext(wac)
                .apply(springSecurity())
                .build();
    }

    // get bookmarks
    @Test
    public void getBookmarks() {
        client.get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(v -> {
                    assertTrue(v.contains("<button type=\"submit\">Add</button>"));
                });
    }

    // add a bookmark
    @Test
    public void addBookmark(@Autowired BookmarkRepository repository) {
        client.post().uri("/new")
                .body(fromFormData("url", "https://www.google.ca"))
                .exchange()
                .expectStatus().is3xxRedirection();

        assertEquals(1, repository.count());
    }

    // bookmark is invalid
    @Test
    public void addInvalidBookmarkFails(@Autowired BookmarkRepository repository) {
        client.post().uri("/new")
                .body(fromFormData("url", "a"))
                .exchange()
                .expectStatus().is3xxRedirection();

        assertEquals(0, repository.count());

        // todo: Couldn't figure out how to test a flash message is added to my session
        //       for the failure. Probably my understanding of mockmvc or how webtestclient
        //       works isn't quite right
    }

    @Test
    public void validateUrls() {
        Map.of(
                "https://cleskowsky.github.io", true,
                "a", false
        ).forEach((key, val) -> {
            assertTrue(BookmarksController.URLValidator.validate(key) == val);
        });
    }

    // todo: Can see getBookmarks when not logged in
    // todo: Can't see add bookmark form when not logged in
    // todo: Can't add a bookmark when not logged in
    // todo: Can add a bookmark when logged in
}
