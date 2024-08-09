package online.talkandtravel.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link JwtService} for handling JWT (JSON Web Token) operations.
 *
 * <p>This service provides methods for creating, validating, and extracting information from JWTs.
 * It uses the HS256 signing algorithm and a base64-encoded secret key for signing the tokens.
 *
 * <p>The service includes the following functionalities:
 * <ul>
 *   <li>{@link #extractUsername(String)} - Extracts the username (subject) from a JWT.</li>
 *   <li>{@link #generateToken(User)} - Generates a JWT with default claims for a given user.</li>
 *   <li>{@link #generateToken(Map, User)} - Generates a JWT with custom claims and user information.</li>
 *   <li>{@link #isTokenValid(String, UserDetails)} - Validates a JWT by checking the username and expiration.</li>
 *   <li>{@link #extractClaim(String, Function)} - Extracts a specific claim from the JWT.</li>
 *   <li>{@link #extractAllClaims(String)} - Extracts all claims from the JWT.</li>
 *   <li>{@link #getSignInKey()} - Retrieves the signing key for JWT creation and validation.</li>
 * </ul>
 *
 * <p>Constants:
 * <ul>
 *   <li>{@code secretKey} - The base64-encoded secret key used for signing and validating the JWTs.</li>
 * </ul>
 *
 * <p>Token Expiration:
 * <ul>
 *   <li>JWTs are set to expire 24 hours (86400000 milliseconds) after issuance.</li>
 * </ul>
 *
 * <p>Exception Handling:
 * <ul>
 *   <li>No explicit exceptions are thrown. The implementation relies on the JWT library to handle parsing and validation errors.</li>
 * </ul>
 */

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${SECRET_KEY}")
    private String secretKey;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * 86400000 milliseconds = 24 hours
     * @param extraClaims
     * @param user
     * @return
     */
    @Override
    public String generateToken(Map<String, Object> extraClaims, User user) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUserEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000 ))
                .signWith(SignatureAlgorithm.HS256, getSignInKey())
                .compact();
    }

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
