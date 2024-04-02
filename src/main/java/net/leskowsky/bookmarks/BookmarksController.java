package net.leskowsky.bookmarks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;

@Controller
public class BookmarksController {

    private final Logger logger = LoggerFactory.getLogger(BookmarksController.class);

    private final BookmarkRepository bookmarkRepository;

    public BookmarksController(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @GetMapping("/")
    public String getBookmarks(Model model) {
        model.addAttribute("urls", bookmarkRepository.findByOrderByIdDesc());
        return "index";
    }

    @PostMapping("/new")
    public RedirectView addBookmark(String url, RedirectAttributes redirectAttributes) {
        logger.info("url=" + url);

        if (UrlValidator.validate(url)) {
            bookmarkRepository.save(new Bookmark(url));
        } else {
            // todo: Put in message bundle
            redirectAttributes.addFlashAttribute("errorMessage", "Sorry but that doesn't look like a valid url.");
        }

        // Redirect client to bookmarks index page
        return new RedirectView("/");
    }
}
