package com.example.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(token);
            } catch (Exception ignored) {}
        }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                } catch (Exception ignored) {}

                if (jwtUtil.validateToken(token)) {
                    // Construir authorities combinando las del usuario en BD (si existe)
                    // y el claim `role` presente en el token (si existe). Esto permite
                    // que tokens antiguos que contienen role=ADMIN sigan funcionando
                    // incluso si por alguna razón la BD no devuelve la autoridad adecuada.
                    java.util.List<GrantedAuthority> authorities = new java.util.ArrayList<>();
                    if (userDetails != null && userDetails.getAuthorities() != null) {
                        authorities.addAll((java.util.Collection<? extends GrantedAuthority>) userDetails.getAuthorities());
                    }
                    String roleFromToken = jwtUtil.extractRole(token);
                    if (roleFromToken != null && !roleFromToken.isBlank()) {
                        // Evitar duplicados sencillos
                        SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority("ROLE_" + roleFromToken);
                        if (!authorities.contains(roleAuthority)) authorities.add(roleAuthority);
                    }

                    if (!authorities.isEmpty()) {
                        Object principal = (userDetails != null) ? userDetails : email;
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                principal, null, authorities
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
        }

        filterChain.doFilter(request, response);
    }
}
