package online.talkandtravel.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GroupMessage {

    public GroupMessage(Long id, String content, LocalDateTime creationDate) {
        this.id = id;
        this.content = content;
        this.creationDate = creationDate;
    }

    public GroupMessage(String content, Country country, User user) {
        this.content = content;
        this.country = country;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 2, max = 1000, message = "The maximum number of characters for a message is 1000")
    private String content;
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime creationDate;
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    @JsonIgnore
    @JsonBackReference
    private Country country;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }
}
