package com.example.CRMGym.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes); // Inicializa la clave secreta
    }

    // Genera un token JWT para un usuario específico
    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username); // Establece el nombre de usuario como sujeto del token
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds); // Calcula la fecha de expiración

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)  // Firma el token con la clave secreta
                .compact();
    }

    // Obtiene el nombre de usuario del token JWT
    public String getUsername(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            LoggerFactory.getLogger(JwtTokenProvider.class).error("Error getting username from token", e);
            return null;
        }    }
    //Valida el token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            LoggerFactory.getLogger(JwtTokenProvider.class).error("Invalid JWT token", e);
            return false;
        }
    }
}
