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

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors()
                // RestAPI 이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
//    @Autowired
//    private UserServiceImpl customUserDetailsService;
//    private PasswordEncoder passwordEncoder;
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
//    }

//    @Autowired
//    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder builder, PasswordEncoder passwordEncoder) throws Exception {
//        // AuthenticationManagerBuilder를 사용하여 AuthenticationManager를 빌드하고 빈으로 등록합니다.
//        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//        return builder.build();
//    }

//    @Bean
//    public void authenticationManager(AuthenticationManagerBuilder builder, PasswordEncoder passwordEncoder) throws Exception {
//        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//    }
//
//
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
//        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//    }
//}
//    private final UserDetailsService userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//    public SecurityConfig(@Qualifier("userSecurityService") UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }
//    @Lazy
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }


//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//    }
    // AuthenticationManager를 빈으로 등록하기 위한 메서드입니다.
//    @Lazy
//    @Bean
//    public AuthenticationManager authenticationManagerBean(AuthenticationManagerBuilder auth) throws Exception {
//        // 여기서 UserDetailsService와 PasswordEncoder를 사용하여 AuthenticationManager를 설정합니다.
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//        return auth.build();
//    }
//
//    // ObjectPostProcessor를 반환하는 메서드가 필요합니다.
//    // 실제로 이 메서드는 Spring Security가 자동으로 처리해주므로,
//    // 직접 구현할 필요는 없습니다. 이 메서드는 예시를 위해 포함되어 있습니다.
//    private ObjectPostProcessor<Object> objectPostProcessor() {
//        // 실제 구현체를 반환해야 합니다.
//        // Spring Security의 기본 구현을 사용하거나, 필요에 따라 커스텀 구현을 제공해야 합니다.
//        return new ObjectPostProcessor<Object>() {
//            @Override
//            public <O> O postProcess(O object) {
//                // 실제 구현은 여기에 작성합니다.
//                return object;
//            }
//        };
//    }

