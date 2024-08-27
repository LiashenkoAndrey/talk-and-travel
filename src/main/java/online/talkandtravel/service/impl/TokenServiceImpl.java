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
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.TokenRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.TokenService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
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

  @Override
  public void deleteUserToken(Long userId) {
    repository.deleteAllByUserId(userId);
  }

  @Override
  public Token save(Token token) {
    return repository.save(token);
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
  @Transactional
  public void validateToken(String token) {
    Long userId = extractId(token);
    verifyProvidedTokenValid(token);
    verifyStoredTokenPresentAndValid(userId);
  }

  @Override
  public Long extractId(String token) {
    String subject = extractClaim(token, Claims::getSubject);
    validateSubject(subject);
    return NumberUtils.toLong(subject);
  }

  @Override
  public String generateToken(User user) {
    return generateToken(new HashMap<>(), user);
  }

  /**
   * 86400000 milliseconds = 24 hours
   */
  @Override
  public String generateToken(Map<String, Object> extraClaims, User user) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(user.getId().toString())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(SignatureAlgorithm.HS256, getSignInKey())
        .compact();
  }

  private void validateSubject(String subject) {
    if (!NumberUtils.isCreatable(subject)) {
      throw new InvalidTokenException("Token subject is not a number",
          "Invalid authentication token");
    }
  }

  public void verifyStoredTokenPresentAndValid(Long userId) {
    Token token = repository.findByUserId(userId).orElseThrow(
        () -> new InvalidTokenException(
            String.format("token of user with id %s not found", userId), "Invalid token"));

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
