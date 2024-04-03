package net.leskowsky.bookmarks;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BookmarkRepository extends CrudRepository<Bookmark, Integer> {

    Iterable<Bookmark> findByStatusOrderByIdDesc(Bookmark.BookmarkStatus status);

    Optional<Bookmark> findByUrl(String url);
}
