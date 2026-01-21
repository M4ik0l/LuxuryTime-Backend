package watchs.luxurytime.Configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import watchs.luxurytime.Support.authentication.JwtAuthConverter;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    public static final String ADMIN = "admin";

    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth
                        //Registrazione utente libera
                        .requestMatchers(HttpMethod.POST, "/utente/registrazione").permitAll()

                        //Lettura prodotti e Ricerca libera (Tutte le GET)
                        .requestMatchers(HttpMethod.GET, "/prodotto/**").permitAll().requestMatchers("/error").permitAll()

                        //Gestione prodotti (POST, PUT, DELETE) solo per ADMIN
                        .requestMatchers(HttpMethod.POST, "/prodotto/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/prodotto/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/prodotto/**").hasRole(ADMIN)

                        //Utenti: la ricerca avanzata degli utenti deve essere solo ADMIN!
                        .requestMatchers("/utente/search").hasRole(ADMIN)
                        .requestMatchers("/utente/tutti_gli_utenti").hasRole(ADMIN)

                        //Ordini e Carrello richiedono login (qualsiasi ruolo)
                        .requestMatchers("/ordine/**").authenticated()
                        .requestMatchers("/carrello/**").authenticated()

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthConverter))
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        // Permettiamo tutti gli header (*) per evitare blocchi strani da Angular
        configuration.setAllowedHeaders(List.of("*"));
        // Permettiamo i credentials (cookie/token auth) se necessari
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
