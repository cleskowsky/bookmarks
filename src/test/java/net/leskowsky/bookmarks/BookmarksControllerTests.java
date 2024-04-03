package net.leskowsky.bookmarks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookmarksControllerTests {

    @Autowired
    WebApplicationContext wac;

    @Autowired
    BookmarkRepository bookmarkRepository;

    private WebTestClient client;

    @BeforeEach
    public void setup() {
        client = MockMvcWebTestClient.bindToApplicationContext(wac)
                .apply(springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .configureClient()
                .build();
    }

    // get bookmarks
    @Test
    public void getBookmarks() {
        client.get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(v -> {
                    assertTrue(v.contains("Login"));
                });
    }

    // add a bookmark
    @Test
    @WithMockUser
    public void addBookmark() {
        var url = "https://addBookmarkTest.leskowsky.net";
        client.post().uri("/new")
                .body(fromFormData("url", url))
                .exchange()
                .expectStatus().is3xxRedirection();

        var result = bookmarkRepository.findByUrl(url);
        assertTrue(result.isPresent());
        assertEquals(url, result.get().getUrl());
        assertEquals(Bookmark.BookmarkStatus.Unread, result.get().getStatus());
    }

    // bookmark is invalid
    @Test
    @WithMockUser
    public void addInvalidBookmarkFails() {
        var url = "a";
        client.post().uri("/new")
                .body(fromFormData("url", url))
                .exchange()
                .expectStatus().is3xxRedirection();

        assertFalse(bookmarkRepository.findByUrl(url).isPresent());

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
            assertEquals(val, UrlValidator.validate(key));
        });
    }

    // todo: Can't add a bookmark when not logged in
    @Test
    public void anonymousUserCantAddBookmarks() {
        client.post().uri("/new")
                .body(fromFormData("url", "http://www.google.ca"))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    // delete a bookmark
    @Test
    @WithMockUser
    public void deleteBookmark() {
        // given a bookmark
        var url = "https://deleteBookmarkTest.leskowsky.net";
        var bookmark = bookmarkRepository.save(new Bookmark(url));

        // when i delete it
        client.post().uri(String.format("/%d/delete", bookmark.getId()))
                .exchange()
                .expectStatus().is3xxRedirection();

        // then it's status is deleted
        var result = bookmarkRepository.findByUrl(url);
        assertTrue(result.isPresent());
        assertEquals(Bookmark.BookmarkStatus.Deleted, result.get().getStatus());
    }
}
