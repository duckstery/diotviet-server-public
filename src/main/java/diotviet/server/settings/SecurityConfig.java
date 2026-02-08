package diotviet.server.settings;

import diotviet.server.exceptions.AuthEntryPointJwt;
import diotviet.server.filters.AuthTokenFilter;
import diotviet.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {

    // ****************************
    // Properties
    // ****************************

    /**
     * User service. This service will be used to load User data for performing authentication
     */
    @Autowired
    UserService userService;
    /**
     * Handle unauthorized request
     */
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // ****************************
    // Public
    // ****************************

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

//    /**
//     * Export WebCustomizer to exclude route with this prefix to be authenticated
//     *
//     * @return
//     */
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers("/js/**", "/images/**");
//    }

    /**
     * AuthenticationProvider bean. Seam like it'll like Laravel User Provider
     *
     * @return
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Create Provider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Return AuthenticationManager from AuthenticationConfiguration
     *
     * @param authConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    /**
     * Return a PasswordEncoder and set it to UserService
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return userService.setPasswordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * Request will be filtered through this chain (authorization process)<br>
     * <li>1. This logic will tell Spring Security how we configure CORS and CSRF</li>
     * <li>2. After that, it'll tell to authenticate or not based on AuthTokenFilter result</li>
     * <li>3. Finally, if AuthTokenFilter is failed, it'll tell to authenticate user by using Username... filter</li>
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(this::configCors)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(this::configExceptionHandling)
                .authenticationProvider(authenticationProvider())
                .sessionManagement(this::configSessionManagement)
                .requiresChannel(this::configRequiresChannel)
                .authorizeHttpRequests(this::configAuthorizeHttpRequests);

        // Set authentication token filter
        // Look like request will be filtered by JWT Token, if failed, proceed to filter by username and password
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Set AuthenticationProvider
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    // ****************************
    // Private
    // ****************************

    /**
     * Config CORS
     *
     * @param configurer
     */
    private void configCors(CorsConfigurer<HttpSecurity> configurer) {
        // Create configuration
        CorsConfiguration configuration = new CorsConfiguration();
        // Set allowed origins
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedOrigins(List.of("http://localhost", "http://localhost:8100", "capacitor://localhost"));
        // Set allowed methods
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        // Set allow credentials
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(List.of("*"));

        // Create source
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Register configuration
        source.registerCorsConfiguration("/**", configuration);

        // Set source
        configurer.configurationSource(source);
    }

    /**
     * Apply exceptionHandling config
     *
     * @param configurer
     */
    private void configExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> configurer) {
        configurer.authenticationEntryPoint(unauthorizedHandler);
    }

    /**
     * Apply exceptionHandling config
     *
     * @param configurer
     */
    private void configSessionManagement(SessionManagementConfigurer<HttpSecurity> configurer) {
        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * Apply requiresChannel config
     *
     * @param configurer
     */
    private void configRequiresChannel(ChannelSecurityConfigurer<HttpSecurity>.ChannelRequestMatcherRegistry configurer) {
//        configurer
//                .anyRequest()
//                .requiresSecure();
    }

    /**
     * Apply authorizeHttpRequests config
     *
     * @param configurer
     */
    private void configAuthorizeHttpRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) {
        configurer
                .requestMatchers("/api/auth/login", "/api/fallback/**").permitAll()
                .requestMatchers("/api/**", "api/auth/logout").authenticated()
                .anyRequest().permitAll();
    }
}
