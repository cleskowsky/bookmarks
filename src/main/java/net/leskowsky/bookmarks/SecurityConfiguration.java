package net.leskowsky.bookmarks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    private final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Value("${SecurityConfiguration.userName}")
    private String userName;

    @Value("${SecurityConfiguration.password}")
    private String password;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var httpSecurity = http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

        httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        httpSecurity.headers(h -> h.frameOptions(fo -> fo.sameOrigin()));

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        var passwd = encoder.encode(password);

        logger.info(String.format("default_user=%s default_password=%s", userName, password));

        UserDetails userDetails = User.withUsername(userName)
                .password(passwd)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }
}
