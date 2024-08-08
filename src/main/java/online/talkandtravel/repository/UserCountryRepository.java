package online.talkandtravel.repository;

import java.util.List;
import java.util.Optional;
import online.talkandtravel.model.entity.UserCountry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCountryRepository extends JpaRepository<UserCountry, Long> {

  List<UserCountry> findByUserId(Long userId);

  Optional<UserCountry> findByCountryNameAndUserId(String countryName, Long userId);
}
