package fr.insee.survey.datacollectionmanagement.user.domain;

import fr.insee.survey.datacollectionmanagement.user.enums.UserRoleTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "InternalUsers")
public class User {

    @Id
    private String identifier;
    private String name;
    private String firstName;
    private String organization;
    private Date creationDate;
    private String creationAuthor;

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Enumerated(EnumType.ORDINAL)
    private UserRoleTypeEnum role;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserEvent> userEvents;

}
