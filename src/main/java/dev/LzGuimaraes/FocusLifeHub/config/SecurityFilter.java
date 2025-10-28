package dev.LzGuimaraes.FocusLifeHub.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenConfig tokenConfig;

    public SecurityFilter(TokenConfig tokenConfig) {
        this.tokenConfig = tokenConfig;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (Strings.isNotEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            Optional<JWTUserData> optUser = tokenConfig.validateToken(token);

            if (optUser.isPresent()) {
                JWTUserData userData = optUser.get();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userData,        
                            null,            
                            Collections.emptyList() 
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
