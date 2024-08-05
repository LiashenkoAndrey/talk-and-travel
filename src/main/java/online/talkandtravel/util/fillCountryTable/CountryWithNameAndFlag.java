package online.talkandtravel.util.fillCountryTable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name ="countries")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CountryWithNameAndFlag {

  @Id
  private String name;

  private String flagCode;

}