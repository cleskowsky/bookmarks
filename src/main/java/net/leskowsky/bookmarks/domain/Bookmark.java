package net.leskowsky.bookmarks.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String url;
    private String title;
    private String description;

    @ManyToMany
    @JoinTable(
            name = "bookmark_tag",
            joinColumns = @JoinColumn(name = "bookmark_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    public enum BookmarkStatus {
        Deleted, Read, Unread
    }

    @Enumerated(EnumType.STRING)
    private BookmarkStatus status = BookmarkStatus.Unread;

    private LocalDateTime createdAt = LocalDateTime.now().plusDays(10);

    public Bookmark() {
    }

    public Bookmark(String url, String title, String description) {
        this.url = url;
        this.title = title;
        this.description = description;
    }
}
