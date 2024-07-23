package online.talkandtravel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Size(min = 2, max = 16, message = "The username must be at least 2 "
            + "and no more than 16 characters long")
    private String userName;
    @Column(nullable = false)
    private String userEmail;
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @Transient
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private Avatar avatar;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Size(min = 10, max = 500, message = "Maximum number of characters for About 500")
    private String about;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Participant> participatedCountries;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Token> tokens;
}
