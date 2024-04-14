package net.leskowsky.bookmarks;

import net.leskowsky.bookmarks.domain.Tag;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Integer> {
}
