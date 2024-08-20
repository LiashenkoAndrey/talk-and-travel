package online.talkandtravel.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.exception.auth.UserAuthenticationException;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.TokenRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link TokenService} for managing authentication and authorization tokens.
 *
 * <p>This service provides methods for CRUD operations related to tokens, including saving,
 * retrieving, and deleting tokens. It ensures token validity and handles batch operations on
 * tokens.
 *
 * <p>The service includes the following functionalities:
 *
 * <ul>
 *   <li>{@link #save(Token)} - Saves a single token to the repository.
 *   <li>{@link #findAllValidTokensByUserId(Long)} - Retrieves a list of valid tokens associated
 *       with a specific user.
 *   <li>{@link #findByToken(String)} - Finds a token by its value, returning an {@code Optional} to
 *       handle the case where the token might not exist.
 *   <li>{@link #saveAll(List)} - Saves a batch of tokens to the repository.
 *   <li>{@link #deleteInvalidTokensByUserId(Long)} - Deletes all invalid tokens associated with a
 *       specific user.
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
  private final TokenRepository repository;

  @Override
  public Token save(Token token) {
    return repository.save(token);
  }

  @Override
  public List<Token> findAllValidTokensByUserId(Long userId) {
    return repository.findAllValidTokensByUserId(userId);
  }

  @Override
  public Optional<Token> findByToken(String token) {
    return repository.findByToken(token);
  }

  @Override
  public List<Token> saveAll(List<Token> tokens) {
    return repository.saveAll(tokens);
  }

  @Override
  public void deleteInvalidTokensByUserId(Long userId) {
    repository.deleteInvalidTokensByUserId(userId);
  }


  private final UserRepository userRepository;

  private UserDetails getUserDetailsByEmail(String email) {
    return userRepository
        .findByUserEmail(email)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Override
  public boolean isValidToken(String token) {
    return !isTokenExpiredAndRevoked(token) && tokenNameMatchesRegisteredUsername(token);
  }



  public boolean tokenNameMatchesRegisteredUsername(String token) {
    String userEmail = extractUsername(token);
    UserDetails userDetails = getUserDetailsByEmail(userEmail);
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public boolean isTokenExpiredAndRevoked(String token) {
    return findByToken(token)
        .map(t -> !t.isRevoked() && !t.isExpired())
        .orElse(false);
  }

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
   *
   * @param extraClaims
   * @param user
   * @return
   */
  @Override
  public String generateToken(Map<String, Object> extraClaims, User user) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(user.getUserEmail())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(SignatureAlgorithm.HS256, getSignInKey())
        .compact();
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
    Claims body;
    try {
      body =
          Jwts.parserBuilder()
              .setSigningKey(getSignInKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
    } catch (Exception e) {
      throw new UserAuthenticationException(e.getMessage());
    }
    return body;
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
