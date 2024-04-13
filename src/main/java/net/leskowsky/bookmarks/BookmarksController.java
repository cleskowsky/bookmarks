package net.leskowsky.bookmarks;

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

    public BookmarksController(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @GetMapping("/")
    public ModelAndView getBookmarks(@RequestParam(name = "showOnly", required = false) Bookmark.BookmarkStatus showOnly) {
        logger.info("controller=bookmarks action=get");

        if (showOnly == null) {
            showOnly = Bookmark.BookmarkStatus.Unread;
        }
        logger.info("showOnly=" + showOnly);

        return new ModelAndView("index",
                Map.of("bookmarks", bookmarkRepository.findByStatusOrderByIdDesc(showOnly)));
    }

    @GetMapping("/new")
    public ModelAndView addBookmark(CreateBookmarkForm createBookmarkForm) {
        var url = createBookmarkForm.getUrl();
        var title = createBookmarkForm.getTitle();
        var description = createBookmarkForm.getDescription();

        logger.info("request_method=get controller=bookmarks action=new url='{}' title='{}' description={}",
                url, title, description.length());

        return new ModelAndView("new", Map.of(
                "url", url,
                "title", title,
                "description", description
        ));
    }

    @PostMapping("/new")
    public RedirectView addBookmark(CreateBookmarkForm createBookmarkForm,
                                    RedirectAttributes redirectAttributes) {
        var url = createBookmarkForm.getUrl();
        var title = createBookmarkForm.getTitle();
        var description = createBookmarkForm.getDescription();

        logger.info("request_method=post controller=bookmarks action=new url='{}' title='{}' description={}",
                url, title, description.length());

        boolean isValid = UrlValidator.validate(url);
        logger.info("is_valid={}", isValid);

        if (isValid) {
            bookmarkRepository.save(new Bookmark(url, title, description));
        } else {
            // todo: Put in message bundle
            redirectAttributes.addFlashAttribute("errorMessage", "Sorry but that doesn't look like a valid url.");
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
