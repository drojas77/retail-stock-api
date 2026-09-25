package com.tienda.GestionStock.config;

import com.tienda.GestionStock.model.Usuario;
import com.tienda.GestionStock.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        //Lo usaremos para crear un usuario de muestra para Docker
        boolean activarUsuario = true;


        if(activarUsuario){
            System.out.println(">>> INICIANDO DataInitializer... count=" + usuarioRepository.count());

            if (!usuarioRepository.existsByUsername("admin")) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");
                // ¡IMPORTANTE! Descomenta o pon lo que tu entidad necesite
                try { admin.setActivo(true); } catch(Exception e) {}

                usuarioRepository.save(admin);
                System.out.println(">>> Usuario de prueba creado: admin / admin123");
            } else {
                System.out.println(">>> Admin ya existía");
            }
        }

    }
}