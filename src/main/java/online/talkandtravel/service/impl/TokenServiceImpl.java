package online.talkandtravel.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.token.InvalidTokenException;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.TokenRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 *       with a specific user.
 *   <li>{@link #findByToken(String)} - Finds a token by its value, returning an {@code Optional} to
 *       handle the case where the token might not exist.
 *   <li>{@link #saveAll(List)} - Saves a batch of tokens to the repository.
 *       specific user.
 * </ul>
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  @Value("${SECRET_KEY}")
  private String secretKey;

  private final TokenRepository repository;
  private final UserRepository userRepository;

  @Override
  public Token save(Token token) {
    return repository.save(token);
  }

  /**
   * Retrieves all valid (non-expired, non-revoked) tokens associated with a given user ID.
   *
   * @param userId The ID of the user whose tokens are to be retrieved.
   * @return A list of valid tokens for the user.
   */


  @Override
  public Optional<Token> findByToken(String token) {
    return repository.findByToken(token);
  }

  @Override
  public List<Token> saveAll(List<Token> tokens) {
    return repository.saveAll(tokens);
  }


  @Override
  @Transactional
  public void validateToken(String token) {
    String userEmail = extractUsername(token);
    verifyProvidedTokenValid(token);
    verifyStoredTokenPresentAndValid(userEmail);
    tokenNameMatchesRegisteredUsername(userEmail);
  }

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

  private UserDetails getUserDetailsByEmail(String email) {
    return userRepository
        .findByUserEmail(email)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  public void tokenNameMatchesRegisteredUsername(String userEmail) {
    UserDetails userDetails = getUserDetailsByEmail(userEmail);
    if (!userEmail.equals(userDetails.getUsername())) {
      String errorMessage = String.format("token %s name don't match with registered username",
          userDetails.getUsername());
      throw new InvalidTokenException(errorMessage, "Invalid authentication token");
    }
  }

  public void verifyStoredTokenPresentAndValid(String userEmail) {
    Token token = repository.findByUserUserEmail(userEmail).orElseThrow(
        () -> new InvalidTokenException(
            String.format("token of user with email %s not found", userEmail), "Invalid token"));

    if (token.isExpired() && token.isRevoked()) {
      String errorMessage = String.format("Token with id:%s is expired or revoked.", token.getId());
      throw new InvalidTokenException(errorMessage, "Token is expired");
    }
  }

  public void verifyProvidedTokenValid(String token) {
    parseClaims(token);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
    final Claims claims = parseClaims(token);

    return claimsResolvers.apply(claims);
  }

  private Claims parseClaims(String token) {
    Claims claims;
    try {
      claims =
          Jwts.parserBuilder()
              .setSigningKey(getSignInKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
    } catch (ExpiredJwtException expiredJwtException) {
      throw new InvalidTokenException(expiredJwtException.getMessage(),
          "Invalid token. The provided token is expired ");
    } catch (Exception e) {
      log.debug(e.getMessage(), e);
      throw new InvalidTokenException(e.getMessage(),
          "Invalid token. Error occupied during token processing");
    }
    return claims;
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
