package net.leskowsky.bookmarks.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookmarkForm {
    String url;
    String title = "";
    String description = "";
    List<Integer> tags = new ArrayList<>();
}
