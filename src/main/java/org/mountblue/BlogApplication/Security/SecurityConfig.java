package org.mountblue.BlogApplication.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC PAGES
                        .requestMatchers(
                                "/",
                                "/dashboard",
                                "/posts/**",
                                "/comments/**",
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        // 🔒 AUTHENTICATED USERS ONLY
                        .requestMatchers(
                                "/newpost",
                                "/posts/*/edit",
                                "/posts/*/update",
                                "/posts/*/delete"
                        ).authenticated()

                        .anyRequest().authenticated()
                )

                // ✅ LOGIN CONFIG
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )

                // ✅ LOGOUT CONFIG
                .logout(logout -> logout
                        .logoutSuccessUrl("/dashboard")
                        .permitAll()
                );

        // ❗ CSRF ENABLED by default (DO NOT disable)
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
