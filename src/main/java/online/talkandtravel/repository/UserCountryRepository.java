package online.talkandtravel.repository;

import java.util.List;
import online.talkandtravel.model.entity.UserCountry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCountryRepository extends JpaRepository<UserCountry, Long> {

  List<UserCountry> findByUserId(Long userId);
}
