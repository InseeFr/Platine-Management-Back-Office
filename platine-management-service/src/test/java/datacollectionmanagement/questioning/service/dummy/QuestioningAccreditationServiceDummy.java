package datacollectionmanagement.questioning.service.dummy;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class QuestioningAccreditationServiceDummy implements QuestioningAccreditationService {
    @Override
    public List<QuestioningAccreditation> findByContactIdentifier(String id) {
        return null;
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
    }

}
