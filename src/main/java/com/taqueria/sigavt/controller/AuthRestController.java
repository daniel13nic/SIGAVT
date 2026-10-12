package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.security.CustomUserDetailsService;
import com.taqueria.sigavt.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> generarToken(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        // validar las credenciales contra la Base de Datos
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // si es correcto, generamos el JWT
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String jwt = jwtUtil.generateToken(userDetails);

        // token en formato JSON
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("tipo", "Bearer");

        return ResponseEntity.ok(response);
    }
}