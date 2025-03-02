package com.doziem.jamTesSystem.constant;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;

public class JwtConstant {

//    @Value("${jwt.secret}")
//    public static String SECRET;

    public static final String SECRET ="grjnkjnksdfangujungfndkjnafsiuQWHIODRQEWJRIUDEWRhmjkyfujngfhjetdhbg";
    public static final String JWT_HEADER ="Authorization";
    public static final long EXPIRATION_TIME = 86400000;

    public static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());


}
