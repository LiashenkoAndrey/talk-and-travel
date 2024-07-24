package online.talkandtravel.repository;

import online.talkandtravel.model.GroupMessage;
import java.util.List;
import java.util.Optional;

import online.talkandtravel.model.dto.IMessageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByCountryIdOrderByCreationDateDesc(Long countryId);

    Optional<IMessageDto> findDistinctById(@Param("id") Long id);
}
