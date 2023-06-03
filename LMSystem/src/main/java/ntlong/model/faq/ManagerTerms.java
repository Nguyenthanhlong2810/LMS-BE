package ntlong.model.faq;

import ntlong.model.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "manager_terms")
public class ManagerTerms extends BaseEntity {

    @Column(length = 10000)
    private String value;
}
