package co.com.wdgg.api.services;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import co.com.wdgg.api.config.JwtConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

@Component
public class JwtService {
    
    private final Mono<SecretKey> secretKeyMono;
    private final JwtConfigurationProperties jwtConfigurationProperties;

    public JwtService(Mono<SecretKey> secretKeyMono, JwtConfigurationProperties jwtConfigurationProperties) {
        this.secretKeyMono = secretKeyMono;
        this.jwtConfigurationProperties = jwtConfigurationProperties;
    }

    private SecretKey getSignInKey() {
        return secretKeyMono.block();
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfigurationProperties.expirationTime()))
                .signWith(getSignInKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build()
                .parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return (String) getClaims(token).get("role");
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
