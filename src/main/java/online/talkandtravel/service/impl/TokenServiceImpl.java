package online.talkandtravel.service.impl;

import online.talkandtravel.model.entity.Token;
import online.talkandtravel.repository.TokenRepository;
import online.talkandtravel.service.TokenService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
