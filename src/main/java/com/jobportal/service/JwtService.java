package com.jobportal.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // In real app, inject from application.properties
    // app.jwt.secret=change-this-to-a-long-random-string
    private static final String SECRET = "change-this-to-a-long-random-secret-key-32-chars-min";

    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 hours

    private final Key key;

    public JwtService() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ------------ GENERATION ------------

    public String generateToken(String email, String role) {
    	 Map<String, Object> claims = new HashMap<>();
    	    claims.put("role", role); 
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
        		.setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ------------ VALIDATION ------------

    public boolean isTokenValid(String token) {
        try {
            // will throw if invalid / expired
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

	public String extractRole(String token) {
		  Claims claims = extractAllClaims(token);
		  Object roleObj = claims.get("role");

		    return roleObj != null ? roleObj.toString() : null;
	}
	
	private Claims extractAllClaims(String token) {
	    return Jwts.parserBuilder()
	            .setSigningKey(key)
	            .build()
	            .parseClaimsJws(token)
	            .getBody();
	}
}
