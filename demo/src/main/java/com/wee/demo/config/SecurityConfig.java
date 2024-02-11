//package com.wee.demo.security;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.BeanIds;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final UserDetailsService userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//
//    // 생성자를 통한 의존성 주입
//    public SecurityConfig(@Qualifier("userSecurityService") UserDetailsService userDetailsService,
//                          PasswordEncoder passwordEncoder) {
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .formLogin(form -> form.disable())
//                .httpBasic(httpBasic -> httpBasic.disable())
//                .sessionManagement(sessionManagement ->
//                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/user/register", "/user/login").permitAll()
//                        .anyRequest().authenticated());
//
//        return http.build();
//    }
//
//    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
//    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder); // 여기서는 메소드 호출이 아닌 주입된 빈을 사용합니다.
//        return auth.build();
//    }
//}
package com.wee.demo.config;

import com.wee.demo.auth.AuthenticationFilter;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Bean
    public AuthenticationFilter authenticationFilter() throws Exception {
        return new AuthenticationFilter(jwtSecret);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/wee",
                                "/wee/user/register",
                                "/wee/user/login",
                                "/wee/user/mypage/**").permitAll()
                        .anyRequest().authenticated()).build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}