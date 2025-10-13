package fr.insee.survey.datacollectionmanagement.questioning.domain;

import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idQuestioningComm_index", columnList = "questioning_id")
})
@NoArgsConstructor
public class QuestioningCommunication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questioning_communication_seq")
    private Long id;

    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    private TypeCommunicationEvent type;

    @ManyToOne
    @JoinColumn(name = "questioning_id")
    private Questioning questioning;
    @Enumerated(EnumType.STRING)
    private StatusCommunication status;

    public QuestioningCommunication(LocalDateTime date, TypeCommunicationEvent type, Questioning questioning, StatusCommunication status) {
        this.date = date;
        this.type = type;
        this.questioning = questioning;
        this.status = status;
    }
}
