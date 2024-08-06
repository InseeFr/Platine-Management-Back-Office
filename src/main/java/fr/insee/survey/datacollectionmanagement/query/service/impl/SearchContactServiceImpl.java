package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchContactDto;
import fr.insee.survey.datacollectionmanagement.query.service.SearchContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;


@Service
@RequiredArgsConstructor
public class SearchContactServiceImpl implements SearchContactService {

    private final ContactService contactService;


    @Override
    public Page<SearchContactDto> searchContactCrossDomain(String param, Pageable pageable) {
        return contactService.findByParameter(StringUtils.upperCase(param), pageable);
    }


}
