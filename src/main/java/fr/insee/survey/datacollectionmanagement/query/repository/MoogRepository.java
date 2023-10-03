package fr.insee.survey.datacollectionmanagement.query.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.impl.AddressServiceImpl;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogExtractionRowDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import fr.insee.survey.datacollectionmanagement.query.dto.MoogQuestioningEventDto;

@Repository
public class MoogRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AddressService addressService;

    final String getEventsQuery = "SELECT qe.id, date, type, survey_unit_id_su, campaign_id "
            + " FROM questioning_event qe join questioning q on qe.questioning_id=q.id join partitioning p on q.id_partitioning=p.id "
            + " WHERE survey_unit_id_su=? AND campaign_id=? ";


    public List<MoogQuestioningEventDto> getEventsByIdSuByCampaign(String idCampaign, String idSu) {
        List<MoogQuestioningEventDto> progress = jdbcTemplate.query(getEventsQuery, new RowMapper<MoogQuestioningEventDto>() {
            public MoogQuestioningEventDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                MoogQuestioningEventDto moogEvent = new MoogQuestioningEventDto();
                moogEvent.setIdManagementMonitoringInfo(rs.getString("id"));
                moogEvent.setStatus(rs.getString("type"));
                moogEvent.setDateInfo(rs.getTimestamp("date").getTime());
                return moogEvent;
            }
        }, new Object[]{idSu, idCampaign});

        return progress;
    }

    final String extractionQuery =
            """
                    select
                    	id_su,
                    	identifier as id_contact,
                    	first_name as firstname,
                    	last_name as lastname,
                    	address_id as address,
                    	date as dateinfo,
                    	type as status,
                    	batch_num
                    from
                    	(
                    	select
                    		id,
                    		campaign_id,
                    		A.id_su,
                    		A.identifier,
                    		first_name,
                    		last_name,
                    		address_id,
                    		id_partitioning as batch_num
                    	from
                    		(
                    		select
                    			campaign_id,
                    			id_su,
                    			contact.identifier,
                    			first_name,
                    			last_name,
                    			address_id
                    		from
                    			view
                    		left join contact on
                    			contact.identifier = view.identifier
                    		where
                    			campaign_id = ?
                    			) as A
                    	left join questioning q on
                    		A.id_su = q.survey_unit_id_su
                    		and q.id_partitioning in (
                    		select
                    				id
                    		from
                    				partitioning p
                    		where
                    				p.campaign_id = ?)
                    				) as B
                    left join questioning_event on
                    	B.id = questioning_event.questioning_id
                    """;


    public List<MoogExtractionRowDto> getExtraction(String idCampaign) {
        List<MoogExtractionRowDto> extraction = jdbcTemplate.query(extractionQuery, new RowMapper<MoogExtractionRowDto>() {

            public MoogExtractionRowDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                MoogExtractionRowDto ev = new MoogExtractionRowDto();

                ev.setAddress("addresse non connue");
                Optional<Address> address = addressService.findById(rs.getLong("address"));

                ev.setStatus(rs.getString("status"));
                ev.setDateInfo(rs.getString("dateinfo"));
                ev.setIdSu(rs.getString("id_su"));
                ev.setIdContact(rs.getString("id_contact"));
                ev.setLastname(rs.getString("lastname"));
                ev.setFirstname(rs.getString("firstname"));
                if (address.isPresent()) {
                    ev.setAddress(address.get().toStringMoog());
                }

                ev.setBatchNumber(rs.getString("batch_num"));

                return ev;
            }
        }, new Object[]{idCampaign});

        return extraction;
    }

    final String surveyUnitFollowUpQuery = """
            select
            	distinct on
            	(id_su) id_su,
            	batch_num,
            	case
            		when type in ('PND') then 1
            		else 0
            	end as PND
            from
            	(
            	select
            		A.id_su,
            		A.identifier,
            		q.id,
            		q.id_partitioning as batch_num
            	from
            		(
            		select
            			id_su,
            			identifier
            		from
            			public.view v
            		where
            			campaign_id = ?)as A
            	left join questioning q on
            		q.survey_unit_id_su = A.id_su
            		and q.id_partitioning in (
            		select
            			id
            		from
            			partitioning p
            		where
            			p.campaign_id = ?)) as B
            left join questioning_event qe on
            	B.id = qe.questioning_id
            where
            	B.id_su not in (
            	select
            		distinct on
            		(id_su) id_su
            	from
            		(
            		select
            			id_su,
            			identifier,
            			id,
            			id_partitioning as batch_num
            		from
            			(
            			select
            				id_su,
            				identifier
            			from
            				public.view
            			where
            				campaign_id = ?)as A
            		left join questioning q on
            			q.survey_unit_id_su = A.id_su
            			and q.id_partitioning in (
            			select
            				id
            			from
            				partitioning p
            			where
            				p.campaign_id = ?)) as B
            	left join questioning_event on
            		B.id = questioning_event.questioning_id
            	where
            		type in ('VALINT', 'VALPAP', 'HC', 'REFUSAL', 'WASTE'))
            order by
            	id_su,
            	pnd desc;
                        
            """;
    ;

    public List<MoogExtractionRowDto> getSurveyUnitToFollowUp(String idCampaign) {

        List<MoogExtractionRowDto> followUp = jdbcTemplate.query(surveyUnitFollowUpQuery,
                new RowMapper<MoogExtractionRowDto>() {
                    public MoogExtractionRowDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        MoogExtractionRowDto er = new MoogExtractionRowDto();
                        er.setIdSu(rs.getString("id_su"));
                        er.setPnd(rs.getInt("PND"));
                        er.setBatchNumber(rs.getString("batch_num"));

                        return er;
                    }
                }, new Object[]{idCampaign, idCampaign});

        return followUp;

    }
}
