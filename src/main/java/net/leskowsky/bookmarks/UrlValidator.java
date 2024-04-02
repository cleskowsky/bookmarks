package net.leskowsky.bookmarks;

import java.net.URI;

public class UrlValidator {
    public static boolean validate(String s) {
        try {
            new URI(s).toURL();
            return true;
        } catch (Exception e) {
            // todo: Send to err reporter
            return false;
        }
    }
}
