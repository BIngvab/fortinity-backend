package cl.fortinity.backend.config;

import cl.fortinity.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;


@Configuration
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;


    @Bean
    public SecretKey secretKey() {

        byte[] bytes = Base64.getDecoder()
                .decode(jwtSecret);

        return new SecretKeySpec(bytes, "HmacSHA256");

    }


    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {

        return NimbusJwtEncoder.withSecretKey(secretKey)
                .build();

    }


    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer("fortinity"));

        return decoder;

    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UsuarioRepository usuarioRepository) throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()


                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/register")
                        .permitAll()


                        .requestMatchers("/api/auth/me")
                        .authenticated()


                        .requestMatchers("/api/usuarios/**")
                        .hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET,
                                "/api/productos/todos")
                        .hasAnyRole("ADMIN", "VENDEDOR")


                        .requestMatchers(HttpMethod.GET,
                                "/api/productos/**",
                                "/api/categorias/**")
                        .permitAll()


                        .requestMatchers(HttpMethod.POST,
                                "/api/productos/**")
                        .hasRole("ADMIN")


                        .requestMatchers(HttpMethod.PUT,
                                "/api/productos/**")
                        .hasRole("ADMIN")


                        .requestMatchers(HttpMethod.DELETE,
                                "/api/productos/**")
                        .hasRole("ADMIN")


                        .requestMatchers(HttpMethod.PATCH,
                                "/api/productos/**")
                        .hasRole("ADMIN")


                        .requestMatchers("/api/categorias/**")
                        .hasRole("ADMIN")


                        .requestMatchers(HttpMethod.POST,
                                "/api/boletas/comprar")
                        .hasRole("CLIENTE")


                        .requestMatchers("/api/boletas/**")
                        .authenticated()


                        .anyRequest()
                        .denyAll())

                .oauth2ResourceServer(oauth2 -> oauth2

                        .jwt(jwt -> jwt.jwtAuthenticationConverter(token -> {

                            var usuario = usuarioRepository
                                    .findByEmail(token.getSubject())
                                    .filter(u -> u.isActivo())
                                    .orElseThrow(() ->
                                            new OAuth2AuthenticationException(
                                                    "invalid_token"));

                            return new JwtAuthenticationToken(
                                    token,
                                    List.of(new SimpleGrantedAuthority(
                                            "ROLE_" + usuario.getRol().name())),
                                    usuario.getEmail());

                        })));


        return http.build();

    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();


        configuration.setAllowedOrigins(
                List.of("http://localhost:3000"));


        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT",
                        "PATCH", "DELETE", "OPTIONS"));


        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type"));


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration("/**", configuration);


        return source;

    }
}