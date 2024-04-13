package net.leskowsky.bookmarks.dto;

import lombok.Data;

@Data
public class CreateBookmarkForm {
    String url;
    String title = "";
    String description = "";
}
