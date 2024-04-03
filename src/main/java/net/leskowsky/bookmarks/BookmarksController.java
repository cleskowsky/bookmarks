package net.leskowsky.bookmarks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Optional;

@Controller
public class BookmarksController {

    private final Logger logger = LoggerFactory.getLogger(BookmarksController.class);

    private final BookmarkRepository bookmarkRepository;

    public BookmarksController(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @GetMapping("/")
    public ModelAndView getBookmarks() {
        logger.info("controller=bookmarks action=get");

        return new ModelAndView("index",
                Map.of("bookmarks", bookmarkRepository.findByStatusOrderByIdDesc(Bookmark.BookmarkStatus.Unread)));
    }

    @PostMapping("/new")
    public RedirectView addBookmark(String url, RedirectAttributes redirectAttributes) {
        logger.info("controller=bookmarks action=new url=" + url);

        boolean isValid = UrlValidator.validate(url);
        logger.info("is_valid=" + isValid);

        if (isValid) {
            bookmarkRepository.save(new Bookmark(url));
        } else {
            // todo: Put in message bundle
            redirectAttributes.addFlashAttribute("errorMessage", "Sorry but that doesn't look like a valid url.");
        }

        // Redirect client to bookmarks index page
        return new RedirectView("/");
    }

    @PostMapping("/{id}/delete")
    public RedirectView deleteBookmark(@PathVariable Integer id) {
        logger.info("controller=bookmarks action=delete id=" + id);

        Optional<Bookmark> result = bookmarkRepository.findById(id);
        if (result.isPresent()) {
            var bookmark = result.get();
            bookmark.setStatus(Bookmark.BookmarkStatus.Deleted);
            bookmarkRepository.save(bookmark);
        }

        return new RedirectView("/");
    }
}
