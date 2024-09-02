package online.talkandtravel.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import online.talkandtravel.model.enums.Role;

/**
 * Entity class representing a user in the application.
 *
 * <p>This class contains information about the user, including:
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the user.
 *   <li>{@code userName} - The username of the user (must be between 2 and 16 characters).
 *   <li>{@code userEmail} - The email address of the user (cannot be null).
 *   <li>{@code password} - The user's password (cannot be null).
 *   <li>{@code role} - The role of the user, which determines their access level (e.g., ADMIN,
 *       USER).
 *   <li>{@code tokens} - List of tokens associated with the user (e.g., for authentication).
 *   <li>{@code avatar} - The avatar associated with the user (optional, lazily loaded).
 *   <li>{@code about} - A brief description about the user (must be between 10 and 500 characters).
 *   <li>{@code countries} - List of countries associated with the user.
 * </ul>
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(
      min = 2,
      max = 16,
      message = "The username must be at least 2 " + "and no more than 16 characters long")
  private String userName;

  @Column(nullable = false)
  private String userEmail;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Builder.Default
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Token> tokens = new ArrayList<>();

  @Transient
  @OneToOne(
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "user",
      fetch = FetchType.LAZY)
  private Avatar avatar;

  @Size(max = 500, message = "Maximum number of characters for About 500")
  private String about;

  @Builder.Default
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserCountry> countries = new ArrayList<>();

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", userName='" + userName + '\'' +
        ", userEmail='" + userEmail + '\'' +
        ", password='" + password + '\'' +
        ", role=" + role +
        ", about='" + about + '\'' +
        '}';
  }
}
