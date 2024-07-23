package online.talkandtravel.repository;

import online.talkandtravel.model.GroupMessage;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByCountryIdOrderByCreationDateDesc(Long countryId);
}
