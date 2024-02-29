package net.leskowsky.bookmarks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookmarksController {

    private final Logger logger = LoggerFactory.getLogger(BookmarksController.class);

    private final BookmarkRepository bookmarkRepository;

    public BookmarksController(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @GetMapping("/")
    public String getBookmarks(Model model) {
        model.addAttribute("urls", bookmarkRepository.findAll());
        return "index";
    }

    @PostMapping("/new")
    public String addBookmark(String url) {
        logger.info("url_to_add=" + url);
        bookmarkRepository.save(new Bookmark(url));
        // Redirect client to bookmarks index page
        return "redirect:";
    }
}
