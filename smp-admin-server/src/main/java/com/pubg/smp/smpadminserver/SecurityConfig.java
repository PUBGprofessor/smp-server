package com.pubg.smp.smpadminserver;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Spring Boot Admin Server 安全配置 (适配 Spring Boot 3.2+)
 * @author pubg (refactored for Spring Security 6)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String adminContextPath;

    public SecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. 定义登录成功的处理器
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");

        http
                // 2. 配置权限拦截 (antMatchers 改为 requestMatchers)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                adminContextPath + "/actuator/**",
                                adminContextPath + "/assets/**",
                                adminContextPath + "/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 3. 配置登录界面
                .formLogin(form -> form
                        .loginPage(adminContextPath + "/login")
                        .successHandler(successHandler)
                )
                // 4. 配置登出
                .logout(logout -> logout.logoutUrl(adminContextPath + "/logout"))
                // 5. 开启 HTTP Basic 支持
                .httpBasic(Customizer.withDefaults())
                // 6. 配置 CSRF 策略 (新版语法)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                "/instances",
                                "/actuator/**",
                                adminContextPath + "/logout"
                        )
                );

        return http.build();
    }
}