package online.talkandtravel.repository;

import online.talkandtravel.model.Token;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT t "
            + "from Token t "
            + "JOIN User u "
            + "ON t.user.id = u.id "
            + "WHERE u.id =:userId "
            + "AND (t.expired = false OR t.revoked = false )"
    )
    List<Token> findAllValidTokensByUserId(Long userId);

    Optional<Token> findByToken(String token);

    @Modifying
    @Query("DELETE "
            + "FROM Token t "
            + "WHERE t.user.id = :userId "
            + "AND (t.expired = true OR t.revoked = true)")
    void deleteInvalidTokensByUserId(Long userId);
}
