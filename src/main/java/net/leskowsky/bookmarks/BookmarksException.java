package net.leskowsky.bookmarks;

public class BookmarksException extends RuntimeException {
    public BookmarksException() {
    }

    public BookmarksException(String message) {
        super(message);
    }

    public BookmarksException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookmarksException(Throwable cause) {
        super(cause);
    }
}
