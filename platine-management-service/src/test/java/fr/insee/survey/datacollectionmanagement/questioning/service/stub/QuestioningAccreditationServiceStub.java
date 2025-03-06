package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Setter
public class QuestioningAccreditationServiceStub implements QuestioningAccreditationService {

    private List<QuestioningAccreditation> questioningAccreditationList;

    @Override
    public List<QuestioningAccreditation> findByContactIdentifier(String id) {
        return questioningAccreditationList;
    }

    @Override
    public Page<QuestioningAccreditation> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public QuestioningAccreditation findById(Long id) {
        return null;
    }

    @Override
    public QuestioningAccreditation saveQuestioningAccreditation(QuestioningAccreditation questioningAccreditation) {
        return null;
    }

    @Override
    public void deleteAccreditation(QuestioningAccreditation c) {
        //not used
    }
}
