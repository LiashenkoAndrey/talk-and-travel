package online.talkandtravel.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_countries")
public class UserCountry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "country_name")
  private Country country;

  @Builder.Default
  @OneToMany(mappedBy = "userCountry", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserChat> chats = new ArrayList<>();
}
