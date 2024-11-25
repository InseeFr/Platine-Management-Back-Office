package fr.insee.survey.datacollectionmanagement.questioning.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idQuestioning_index", columnList = "questioning_id")
})
public class QuestioningEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questioning_event_seq")
    private Long id;

    private Date date;
    @Enumerated(EnumType.STRING)
    private TypeQuestioningEvent type;

    @ManyToOne
    private Questioning questioning;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_upload")
    @JsonManagedReference
    private Upload upload;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;

    public QuestioningEvent(Date date, TypeQuestioningEvent type, Questioning questioning) {
        this.date = date;
        this.type = type;
        this.questioning = questioning;
    }

    public QuestioningEvent() {
    }

    @Override
    public String toString() {
        return "QuestioningEvent [id=" + id + ", date=" + date + ", type=" + type + ", payload=" + payload
                + "]";
    }

}
