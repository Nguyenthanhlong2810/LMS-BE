package ntlong.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Setter
@Getter
public class Experience extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;
}
