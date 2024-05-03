package net.leskowsky.bookmarks;

import net.leskowsky.bookmarks.domain.Bookmark;
import net.leskowsky.bookmarks.domain.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookmarkRepositoryTests {

    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private TagRepository tagRepository;

    // without filter, get unread bookmarks sorted by date desc
    @Test
    public void getBookmarks() {
        // given a few bookmarks, return them in date order desc
        var bm = new Bookmark("url", "title", "description");
        bm.setCreatedAt(LocalDateTime.now());
        bookmarkRepository.save(bm);

        bm = new Bookmark("url2", "title", "description");
        bm.setCreatedAt(LocalDateTime.now().plusDays(1));
        bookmarkRepository.save(bm);

        // when i search for unread bookmarks
        var bookmarks = bookmarkRepository.findByStatusOrderByIdDesc(Bookmark.BookmarkStatus.Unread);

        // then i get a list of bookmarks in sort order createdAt desc
        assertEquals(2, bookmarks.size());
        assertEquals("url2", bookmarks.get(0).getUrl());
        assertEquals("url", bookmarks.get(1).getUrl());
    }

    // with tag filter, get unread bookmarks with tag sorted by date desc
    @Test
    public void getBookmarksByTag() {
        // given 2 bookmarks tagged by 'getBookmarksByTagTest'
        var tag = tagRepository.save(new Tag("getBookmarksByTagTest"));

        var bm = new Bookmark("url", "title", "description");
        bm.getTags().add(tag);
        bookmarkRepository.save(bm);

        bm = new Bookmark("url2", "title", "description");
        bm.getTags().add(tag);
        bookmarkRepository.save(bm);

        // when I fetch them from the db
        var bookmarks = bookmarkRepository.findByTagsId(tag.getId());

        // then I should get them back in most recent first order
        assertEquals(2, bookmarks.size());
        assertEquals("url2", bookmarks.get(0).getUrl());
    }

    // with read filter, get read bookmarks sorted by date desc
    // with read and tag filters, get bookmarks that have been read with tag by date desc
    // editing a bookmark should preserve tags selected
}
