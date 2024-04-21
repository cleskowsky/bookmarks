package net.leskowsky.bookmarks.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class App {

    @Value("${App.homeUrl}")
    public String homeUrl;
}
