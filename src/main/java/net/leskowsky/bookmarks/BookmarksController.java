package net.leskowsky.bookmarks;

import net.leskowsky.bookmarks.domain.Bookmark;
import net.leskowsky.bookmarks.dto.CreateBookmarkForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
public class BookmarksController {

    private final Logger logger = LoggerFactory.getLogger(BookmarksController.class);

    private final BookmarkRepository bookmarkRepository;
    private final TagRepository tagRepository;

    public BookmarksController(BookmarkRepository bookmarkRepository, TagRepository tagRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/")
    public ModelAndView getBookmarks(@RequestParam(name = "showOnly", required = false) Bookmark.BookmarkStatus showOnly,
                                     @RequestParam(name = "q", required = false) String q) {
        logger.info("controller=bookmarks action=get");

        if (q != null) {
            logger.info("q=" + q);
            var tag = tagRepository.findByName(q);
            var bookmarks = bookmarkRepository.findByTagsId(tag.getId());
            return new ModelAndView("index", Map.of("bookmarks", bookmarks));
        }

        // todo: filters should combine
        // todo: use q for all kinds of filtering
        if (showOnly == null) {
            logger.info("showOnly=Unread");
            var bookmarks = bookmarkRepository.findByStatusOrderByIdDesc(Bookmark.BookmarkStatus.Unread);
            return new ModelAndView("index", Map.of("bookmarks", bookmarks));
        } else {
            logger.info("showOnly=Read");
            var bookmarks = bookmarkRepository.findByStatusOrderByIdDesc(Bookmark.BookmarkStatus.Read);
            return new ModelAndView("index", Map.of("bookmarks", bookmarks));
        }
    }

    @GetMapping("/new")
    public ModelAndView addBookmark(CreateBookmarkForm createBookmarkForm) {
        var url = createBookmarkForm.getUrl();
        var title = createBookmarkForm.getTitle();
        var description = createBookmarkForm.getDescription();

        logger.info("request_method=get controller=bookmarks action=new url={} title={} description={}",
                url, title, description.length());

        return new ModelAndView("new", Map.of(
                "url", url,
                "title", title,
                "description", description,
                "tags", tagRepository.findAll()
        ));
    }

    @PostMapping("/new")
    public RedirectView addBookmark(CreateBookmarkForm createBookmarkForm,
                                    RedirectAttributes redirectAttributes) {
        var url = createBookmarkForm.getUrl();
        var title = createBookmarkForm.getTitle();
        var description = createBookmarkForm.getDescription();

        logger.info("request_method=post " +
                        "controller=bookmarks " +
                        "action=new url={} " +
                        "title={} " +
                        "description={} " +
                        "tags={}",
                url, title, description.length(), createBookmarkForm.getTagId());

        boolean isValid = UrlValidator.validate(url);
        logger.info("is_valid={}", isValid);

        if (isValid) {
            var bookmark = new Bookmark(url, title, description);
            createBookmarkForm.getTagId().forEach(id -> {
                var result = tagRepository.findById(id);
                if (result.isPresent()) {
                    var tag = result.get();
                    bookmark.getTags().add(tag);
                }
            });
            bookmarkRepository.save(bookmark);
        } else {
            // todo: Put in message bundle
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid url");
        }

        return new RedirectView("/");
    }

    @PostMapping("/{id}/delete")
    public RedirectView deleteBookmark(@PathVariable int id) {
        logger.info("controller=bookmarks action=delete id=" + id);
        bookmarkRepository.setStatusById(Bookmark.BookmarkStatus.Deleted, id);
        return new RedirectView("/");
    }

    @PostMapping("/{id}/markRead")
    public RedirectView markRead(@PathVariable int id) {
        logger.info("controller=bookmarks action=markRead id=" + id);
        bookmarkRepository.setStatusById(Bookmark.BookmarkStatus.Read, id);
        return new RedirectView("/");
    }
}
