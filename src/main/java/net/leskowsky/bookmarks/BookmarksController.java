package net.leskowsky.bookmarks;

import net.leskowsky.bookmarks.domain.Bookmark;
import net.leskowsky.bookmarks.dto.BookmarkForm;
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
                                     @RequestParam(name = "q", defaultValue = "") String q) {
        logger.info("controller=bookmarks action=get");

        // todo: make a parser for q that makes a query filter
        // todo: use q for all filtering
        if (!q.isEmpty()) {
            logger.info("q=" + q);
            var tag = tagRepository.findByName(q);
            var bookmarks = bookmarkRepository.findByTagsIdOrderByIdDesc(tag.getId());
            return new ModelAndView("index", Map.of(
                    "bookmarks", bookmarks,
                    "q", q
            ));
        }

        // todo: filters should combine
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
    public ModelAndView addBookmark(BookmarkForm form) {
        var url = form.getUrl();
        var title = form.getTitle();
        var description = form.getDescription();

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
    public RedirectView addBookmark(BookmarkForm form,
                                    RedirectAttributes redirectAttributes) {
        var url = form.getUrl();
        var title = form.getTitle();
        var description = form.getDescription();

        logger.info("request_method=post controller=bookmarks action=new url={} title={} description={} tags={}",
                url, title, description.length(), form.getTags());

        boolean isValid = UrlValidator.validate(url);
        logger.info("is_valid={}", isValid);

        if (isValid) {
            var bookmark = new Bookmark(url, title, description);
            form.getTags().forEach(id -> {
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

    @GetMapping("/{id}/edit")
    public ModelAndView editBookmark(@PathVariable int id,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam(defaultValue = "") String q) {
        logger.info("request_method=get controller=bookmarks action=edit id={}", id);

        var result = bookmarkRepository.findById(id);
        if (result.isPresent()) {
            var bookmark = result.get();
            return new ModelAndView("edit", Map.of(
                    "bm", bookmark,
                    "allTags", tagRepository.findAll(),
                    "q", q
            ));
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Invalid id");
        return new ModelAndView("index", Map.of("q", q));
    }

    @PostMapping("/{id}/edit")
    public RedirectView editBookmark(@PathVariable int id,
                                     BookmarkForm form,
                                     @RequestParam(defaultValue = "") String q) {
        logger.info("request_method=post controller=bookmarks action=edit id={}", id);

        var result = bookmarkRepository.findById(id);
        if (result.isPresent()) {
            var bm = result.get();
            bm.setTitle(form.getTitle());
            bm.setUrl(form.getUrl());
            bm.setDescription(form.getDescription());
            bm.getTags().clear();
            form.getTags().forEach(tagId -> {
                bm.getTags().add(tagRepository.findById(tagId).get());
            });
            bookmarkRepository.save(bm);
        }

        return new RedirectView("/?q=" + q);
    }

    @PostMapping("/{id}/delete")
    public RedirectView deleteBookmark(@PathVariable int id,
                                       @RequestParam(defaultValue = "") String q) {
        logger.info("controller=bookmarks action=delete id=" + id);
        bookmarkRepository.deleteById(id);
        return new RedirectView("/?q=" + q);
    }

    @PostMapping("/{id}/markRead")
    public RedirectView markRead(@PathVariable int id,
                                 @RequestParam(defaultValue = "") String q) {
        logger.info("controller=bookmarks action=markRead id=" + id);
        bookmarkRepository.setStatusById(Bookmark.BookmarkStatus.Read, id);
        return new RedirectView("/?q=" + q);
    }
}
