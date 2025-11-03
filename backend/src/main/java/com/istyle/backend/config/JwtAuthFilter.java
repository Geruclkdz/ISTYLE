package com.istyle.backend.config;

import com.istyle.backend.service.JwtInterface;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtInterface jwtInterface;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();

        // 1) Bypass public endpoints completely
        if (path.startsWith("/images/")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No JWT present → continue unauthenticated
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        try {
            final String userEmail = jwtInterface.extractEmail(jwtToken);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtInterface.isTokenValid(jwtToken, user)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // Always continue the chain, regardless of whether auth was set
            filterChain.doFilter(request, response);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Keep your existing explicit 401 behavior for expired tokens
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String errorJson = "{\"error\": \"Unauthorized\", \"message\": \"JWT Token expired.\"}";
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(errorJson);
        } catch (io.jsonwebtoken.JwtException e) {
            // Any other JWT parsing/validation exception → treat as unauthenticated and continue
            // Option A (relaxed): continue unauthenticated
            // filterChain.doFilter(request, response);
            // return;

            // Option B (strict, similar to expired): send 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String errorJson = "{\"error\": \"Unauthorized\", \"message\": \"Invalid JWT token.\"}";
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(errorJson);
        }
    }

}
