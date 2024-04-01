package net.leskowsky.bookmarks;

import org.springframework.data.repository.CrudRepository;

public interface BookmarkRepository extends CrudRepository<Bookmark, Integer> {

    Iterable<Bookmark> findByOrderByIdDesc();
}
