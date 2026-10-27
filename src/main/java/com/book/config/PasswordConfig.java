package com.book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置。
 *
 * <p>这里只注册一个 {@link PasswordEncoder} Bean，不引入完整的安全过滤链：
 * 依赖中仅有 spring-security-crypto，没有 spring-boot-starter-security，
 * 因此不会出现 Spring Security 默认的登录拦截页。
 */
@Configuration
public class PasswordConfig {

    /** BCrypt 强度 10，单次哈希约 50~100ms，兼顾安全与登录体验。 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
