package net.leskowsky.bookmarks;

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
    public RedirectView addBookmark(String url, String title, RedirectAttributes redirectAttributes) {
        logger.info("controller=bookmarks action=new url='{}' title='{}'", url, title);

        boolean isValid = UrlValidator.validate(url);
        logger.info("is_valid={}", isValid);

        if (isValid) {
            bookmarkRepository.save(new Bookmark(url, title));
        } else {
            // todo: Put in message bundle
            redirectAttributes.addFlashAttribute("errorMessage", "Sorry but that doesn't look like a valid url.");
        }

        // Redirect client to bookmarks index page
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
