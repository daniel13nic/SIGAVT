package com.taqueria.sigavt.security;

import com.taqueria.sigavt.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()

                        // acceso público al endpoint para pedir el Token
                        .requestMatchers("/api/auth/login").permitAll()

                        // Proteger todo el resto de la API REST
                        .requestMatchers("/api/**").authenticated()

                        .requestMatchers("/", "/inicio", "/ventas/**").hasAnyRole("OPERADOR", "ADMINISTRADOR")
                        .requestMatchers("/productos/**", "/proveedores/**", "/usuarios/**", "/reportes/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login").defaultSuccessUrl("/inicio", true).permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll()
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"))

                // Filtro JWT
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
    @Bean
    public CommandLineRunner inicializarCredenciales(UsuarioRepository usuarioRepo, PasswordEncoder encoder) {
        return args -> {
            usuarioRepo.findByUsername("danieln").ifPresent(usuario -> {
                if (!usuario.getPassword().startsWith("$2a$")) {
                    usuario.setPassword(encoder.encode("admin123"));
                    usuarioRepo.save(usuario);
                    System.out.println("==== CREDENCIAL ENCRIPTADA CON ÉXITO PARA: danieln ====");
                }
            });
        };
    }
*/

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}