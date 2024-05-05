package net.leskowsky.bookmarks;

import jakarta.transaction.Transactional;
import net.leskowsky.bookmarks.domain.Bookmark;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends CrudRepository<Bookmark, Integer> {

    List<Bookmark> findByStatusOrderByIdDesc(Bookmark.BookmarkStatus status);

    Optional<Bookmark> findByUrl(String url);

    @Modifying
    @Transactional
    @Query("update Bookmark set status = ?1 where id = ?2")
    void setStatusById(Bookmark.BookmarkStatus status, int id);

    List<Bookmark> findByTagsIdOrderByIdDesc(long id);
}
