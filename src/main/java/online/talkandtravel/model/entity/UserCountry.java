package online.talkandtravel.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing the association between a user and a country.
 *
 * <p>This class is used to manage the relationship of a user with a country, including:
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the user-country association.
 *   <li>{@code user} - The user associated with the country.
 *   <li>{@code country} - The country associated with the user.
 *   <li>{@code chats} - The list of user-chats associated with the user and country.
 * </ul>
 */
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
