package ru.nikiwhite.employeeservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String JWTSecret;

    public String generateToken(String email) {
        return JWT.create()
                .withSubject("Employee details")
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withIssuer("Employee Service")
                .withExpiresAt(ZonedDateTime.now().plusMinutes(60).toInstant())
                .sign(Algorithm.HMAC256(JWTSecret));
    }

    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(JWTSecret))
                .withSubject("Employee details")
                .withIssuer("Employee Service")
                .build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        return decodedJWT.getClaim("email").asString();
    }
}
