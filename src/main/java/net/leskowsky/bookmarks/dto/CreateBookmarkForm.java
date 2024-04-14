package net.leskowsky.bookmarks.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateBookmarkForm {
    String url;
    String title = "";
    String description = "";
    List<Integer> tagIds = new ArrayList<>();
}
