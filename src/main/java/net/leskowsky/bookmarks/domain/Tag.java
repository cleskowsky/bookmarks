package net.leskowsky.bookmarks.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Bookmark> bookmarks = new HashSet<>();

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }
}
