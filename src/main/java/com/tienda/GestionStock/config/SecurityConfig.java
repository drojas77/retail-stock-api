package com.tienda.GestionStock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas públicas (Manejo de errores)
                        .requestMatchers("/error/**").permitAll()

                  // Rutas accesibles tanto para VENDEDOR como ADMIN (p. ej., consultar catálogo o realizar ventas)
                        .requestMatchers("/"
                                , "/index"
                                ,"/stock"
                                , "/stock/ver"
                                , "/api/ventas/**","/ventas"
                                , "/ventas/mostrador"
                                , "/ventas/procesar"
                                ).hasAnyRole("VENDEDOR","ADMIN")

                        // Rutas exclusivas para administradores (p. ej., introducir/editar stock)
                        .requestMatchers(
                                "/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                                // Redirección personalizada para el error de acceso denegado (403)
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/error/403")
                );

        return http.build();
    }

//Deshabilitar el login
/*    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permitir absolutamente TODO sin autenticación
                        .anyRequest().permitAll()
                )
                // Desactivamos el formulario de login y la autenticación básica
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }*/

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}