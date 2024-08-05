package online.talkandtravel.repository;

import online.talkandtravel.model.entity.Avatar;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByUserId(Long userId);
}
