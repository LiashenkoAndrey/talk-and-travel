package online.talkandtravel.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.token.ExpiredTokenException;
import online.talkandtravel.exception.token.InvalidTokenException;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.TokenType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.TokenRepository;
import online.talkandtravel.service.TokenService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
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
 *   <li>{@link #save(Token)} - Saves a single token to the repository. with a specific user.
 *   <li>{@link #findByToken(String)} - Finds a token by its value, returning an {@code Optional} to
 *       handle the case where the token might not exist.
 *   <li>{@link #saveAll(List)} - Saves a batch of tokens to the repository. specific user.
 * </ul>
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  public static final String TOKEN_OF_USER_NOT_FOUND = "Token for user with id %s is not found";
  public static final String TOKEN_NOT_FOUND = "Token is not found";

  @Value("${SECRET_KEY}")
  private String secretKey;

  private final TokenRepository tokenRepository;

  @Override
  public Token generatePasswordRecoveryToken(User user) {
    log.info("Generate password recovery token for user: {}", user.getId());
    Token tempToken = Token.builder()
        .token(UUID.randomUUID().toString())
        .expired(false)
        .revoked(false)
        .tokenType(TokenType.PASSWORD_RECOVERY)
        .expiresAt(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(30))
        .user(user)
        .build();
    return tokenRepository.save(tempToken);
  }

  @Override
  public void validatePasswordRecoveryToken(Token token) {
    log.info("Validate recovery token with id: {}", token);
    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    if (token.getExpiresAt().isBefore(now)) {
      log.info("Recovery token with id: {} is expired", token.getId());
      throw new ExpiredTokenException(token.getId());
    }
    log.info("Recovery token with id: {} validation successful ", token.getId());
  }

  @Override
  public void deleteToken(Token token) {
    log.info("Delete token with id: {}", token);
    tokenRepository.delete(token);
  }

  @Override
  public Token getToken(String tokenStr) {
    return tokenRepository.findByToken(tokenStr)
        .orElseThrow(() -> new InvalidTokenException(TOKEN_NOT_FOUND, "Invalid token"));
  }

  @Override
  @Transactional
  public void deleteUserToken(Long userId) {
    tokenRepository.deleteAllByUserId(userId);
  }

  @Override
  public Token save(Token token) {
    return tokenRepository.save(token);
  }

  @Override
  public Optional<Token> findByToken(String token) {
    return tokenRepository.findByToken(token);
  }

  @Override
  public List<Token> saveAll(List<Token> tokens) {
    return tokenRepository.saveAll(tokens);
  }

  /**
   * @param token Authentication token from header
   * @return ID of authenticated user
   */
  @Override
  @Transactional
  public Long validateTokenAndGetUserId(String token) {
    Long userId = extractId(token);
    verifyProvidedTokenValid(token);
    verifyStoredTokenPresentAndValid(userId);
    return userId;
  }

  @Override
  public Long extractId(String token) {
    String subject = extractClaim(token, Claims::getSubject);
    validateSubject(subject);
    return NumberUtils.toLong(subject);
  }

  @Override
  public String generateToken(Long userId) {
    return generateToken(new HashMap<>(), userId);
  }

  /** 86400000 milliseconds = 24 hours */
  @Override
  public String generateToken(Map<String, Object> extraClaims, Long userId) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userId.toString())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(SignatureAlgorithm.HS256, getSignInKey())
        .compact();
  }

  private void validateSubject(String subject) {
    if (!NumberUtils.isCreatable(subject)) {
      throw new InvalidTokenException(
          "Token subject is not a number", "Invalid authentication token");
    }
  }

  private void verifyStoredTokenPresentAndValid(Long userId) {
    Token token =
        tokenRepository.findAllByUserId(userId).stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new InvalidTokenException(
                        String.format(TOKEN_OF_USER_NOT_FOUND, userId), "Invalid token"));

    if (token.isExpired() && token.isRevoked()) {
      String errorMessage = String.format("Token with id :%s is expired or revoked.", token.getId());
      throw new InvalidTokenException(errorMessage, "Token is expired");
    }
  }

  private void verifyProvidedTokenValid(String token) {
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
      throw new InvalidTokenException(
          expiredJwtException.getMessage(), "Invalid token. The provided token is expired ");
    } catch (Exception e) {
      throw new InvalidTokenException(
          e.getMessage(), "Invalid token. Error occupied during token processing");
    }
    return claims;
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
