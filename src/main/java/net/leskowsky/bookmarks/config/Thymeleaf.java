package net.leskowsky.bookmarks.config;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Thymeleaf {

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
