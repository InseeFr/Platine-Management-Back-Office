package fr.insee.survey.datacollectionmanagement.questioning.domain;

import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idQuestioningComm_index", columnList = "questioning_id")
})
public class QuestioningCommunication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questioning_communication_seq")
    private Long id;

    private Date date;
    @Enumerated(EnumType.STRING)
    private TypeCommunicationEvent type;

    @ManyToOne
    @JoinColumn(name = "questioning_id")
    private Questioning questioning;
    @Enumerated(EnumType.STRING)
    private StatusCommunication status;

}
