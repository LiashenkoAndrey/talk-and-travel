package online.talkandtravel.repository;

import online.talkandtravel.model.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
  @Query("SELECT COUNT(c) FROM Country c")
  long countCountries();
}
