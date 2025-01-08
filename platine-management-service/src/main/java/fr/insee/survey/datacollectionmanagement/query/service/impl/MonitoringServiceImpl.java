package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.query.dto.MoogFollowUpDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogProgressDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogRowProgressDto;
import fr.insee.survey.datacollectionmanagement.query.repository.MonitoringRepository;
import fr.insee.survey.datacollectionmanagement.query.service.MonitoringService;
import fr.insee.survey.datacollectionmanagement.util.JSONCollectionWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    private final MonitoringRepository monitoringRepository;

    @Override
    public JSONCollectionWrapper<MoogProgressDto> getProgress(String idCampaign) {
        List<MoogRowProgressDto> rows = monitoringRepository.getProgress(idCampaign);

        HashMap<String, MoogProgressDto> lots = new HashMap<String, MoogProgressDto>();

        if (!rows.isEmpty()) {
            for (MoogRowProgressDto row : rows) {

                String batchNumber = row.getBatchNum();

                if (!lots.containsKey(String.valueOf(batchNumber))) {
                    lots.put(String.valueOf(batchNumber), new MoogProgressDto(batchNumber));
                }

                MoogProgressDto lot = lots.get(String.valueOf(batchNumber));

                lot.setNbSu(lot.getNbSu() + row.getTotal());

                switch (row.getStatus().trim()) {
                    case "REFUSAL":
                        lot.setNbRefusal(row.getTotal());
                        break;
                    case "VALINT":
                        lot.setNbIntReceived(row.getTotal());
                        break;
                    case "VALPAP":
                        lot.setNbPapReceived(row.getTotal());
                        break;
                    case "HC":
                        lot.setNbHC(row.getTotal());
                        break;
                    case "PARTIELINT":
                        lot.setNbIntPart(row.getTotal());
                        break;
                    case "WASTE":
                        lot.setNbOtherWastes(row.getTotal());
                        break;
                    case "PND":
                        lot.setNbPND(row.getTotal());
                        break;
                    case "INITLA":
                        break;
                }
            }
        }
        return new JSONCollectionWrapper<>(lots.values());
    }

    @Override
    public JSONCollectionWrapper<MoogFollowUpDto> getFollowUp(String idCampaign) {
        return new JSONCollectionWrapper<MoogFollowUpDto>(monitoringRepository.getFollowUp(idCampaign));
    }
}
