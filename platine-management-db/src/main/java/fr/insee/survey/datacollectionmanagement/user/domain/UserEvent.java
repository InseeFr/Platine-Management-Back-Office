package fr.insee.survey.datacollectionmanagement.user.domain;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.user.enums.UserEventTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_event_seq")
    private Long id;

    private Date eventDate;

    @NonNull
    @Enumerated(EnumType.ORDINAL)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private UserEventTypeEnum type;

    @ManyToOne
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;

    @Override
    public String toString() {
        return "UserEvent{" +
                "id=" + id +
                ", eventDate=" + eventDate +
                ", type=" + type +
                ", user=" + user +
                ", payload=" + payload +
                '}';
    }
}
