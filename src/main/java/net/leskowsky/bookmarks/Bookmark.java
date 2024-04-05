package net.leskowsky.bookmarks;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String url;

    enum BookmarkStatus {
        Deleted,
        Read,
        Unread
    }

    @Enumerated(EnumType.STRING)
    private BookmarkStatus status = BookmarkStatus.Unread;

    public Bookmark() {
    }

    public Bookmark(String url) {
        this.url = url;
    }
}
