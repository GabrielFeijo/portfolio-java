package br.com.gabrielfeijo.portfolio.infrastructure.configuration;

import br.com.gabrielfeijo.portfolio.infrastructure.security.ApiKeyAuthenticationFilter;
import br.com.gabrielfeijo.portfolio.infrastructure.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    @Value("${portfolio.security.swagger-user:admin}")
    private String swaggerUser;

    @Value("${portfolio.security.swagger-password:admin_password_here}")
    private String swaggerPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username(swaggerUser)
                .password(passwordEncoder.encode(swaggerPassword))
                .roles("SWAGGER_ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints Públicos de Raiz e Health
                        .requestMatchers(HttpMethod.GET, "/", "/v2", "/v2/", "/health", "/v2/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Swagger / OpenAPI UI
                        .requestMatchers("/swagger/**", "/swagger-ui/**", "/swagger-ui.html", "/v2/api-docs/**", "/v2/api-docs", "/v3/api-docs/**", "/v3/api-docs").permitAll()

                        // Endpoints Públicos de Comandos (Consulta)
                        .requestMatchers(HttpMethod.GET, "/v2/command", "/v2/command/**").permitAll()

                        // Endpoints Públicos de Reviews (Consulta e Criação Pública)
                        .requestMatchers(HttpMethod.GET, "/v2/review", "/v2/review/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v2/review").permitAll()

                        // Endpoints Públicos de Contato
                        .requestMatchers(HttpMethod.POST, "/v2/contact").permitAll()

                        // Endpoints Administrativos Protegidos por API Key
                        .requestMatchers(HttpMethod.POST, "/v2/command").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v2/command/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v2/command/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/v2/review/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v2/review/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
