package net.leskowsky.bookmarks;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

    private LocalDateTime createdAt = LocalDateTime.now().plusDays(10);

    public Bookmark() {
    }

    public Bookmark(String url) {
        this.url = url;
    }
}
