package com.doziem.jamTesSystem.config;


import com.doziem.jamTesSystem.constant.JwtConstant;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

import static com.doziem.jamTesSystem.constant.JwtConstant.SECRET;
import static com.doziem.jamTesSystem.constant.JwtConstant.SECRET_KEY;

@Component
public class JwtUtil {

    private SecretKey getKey(){
      return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername()) // Set the subject (username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JwtConstant.EXPIRATION_TIME))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }


    
    private <T>T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return  Jwts.parser()
                .verifyWith(getKey()) // Verify JWT with the signing key
                .build()
                .parseSignedClaims(token) // Parses the JWT
                .getPayload(); // Extracts claims directly
    }

//    public boolean validateToken(String token) {
//
//        final String username = extractUsername(token);
//        System.out.println("Extracted Username: " + username);
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return false; // No authenticated user
//        }
//
//        Object principal = authentication.getPrincipal();
//
//        if (principal instanceof UserDetails userDetails) { // Using pattern matching for instanceof
//            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
//        }
//
//        return false;
//    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) SECRET_KEY)
                    .build()
                    .parseSignedClaims(token); // New method for parsing claims

            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid token format: " + e.getMessage());
        }  catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

}
