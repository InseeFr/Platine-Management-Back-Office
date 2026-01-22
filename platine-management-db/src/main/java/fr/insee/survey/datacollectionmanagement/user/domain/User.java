package fr.insee.survey.datacollectionmanagement.user.domain;

import fr.insee.survey.datacollectionmanagement.user.enums.UserRoleTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "internal_users")
public class User {

    @Id
    private String identifier;

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Enumerated(EnumType.ORDINAL)
    private UserRoleTypeEnum role;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private Set<UserEvent> userEvents;

}
