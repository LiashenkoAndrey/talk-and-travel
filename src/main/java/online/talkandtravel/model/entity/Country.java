package online.talkandtravel.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {

  public Country(String name, String flagCode) {
    this.name = name;
    this.flagCode = flagCode;
  }

  @Id
  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String flagCode;

  @Builder.Default
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "country_id")
  private List<Chat> chats = new ArrayList<>();
}
