package com.example.CRMGym.security;

import com.example.CRMGym.services.TokenBlacklist;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklist tokenBlacklist;


    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, TokenBlacklist tokenBlacklist) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklist = tokenBlacklist;
    }

    // Filtro principal que se ejecuta en cada solicitud
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        //ignorar actuator
        String path = request.getRequestURI();
        if (path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);  // Resuelve el token desde la solicitud
        if (token != null && jwtTokenProvider.validateToken(token) && !tokenBlacklist.isBlacklisted(token)) {  // Valida el token, tambien en la blacklist
            String username = jwtTokenProvider.getUsername(token); // Obtiene el nombre de usuario del token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Carga los detalles del usuario

            if(userDetails !=null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);  // Establece la autenticación en el contexto de seguridad
                log.debug("Authenticated user: " + username + ", setting security context"); // Registra el usuario autenticado
            }
        }else{
            log.debug("No valid JWT token found, uri: " + request.getRequestURI()); // Registra si no se encuentra un token válido
        }
        filterChain.doFilter(request, response); // Continúa con la cadena de filtros
    }

    // Resuelve el token desde el encabezado de la solicitud
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Retira el prefijo "Bearer " del token
        }
        return null;
    }
}
