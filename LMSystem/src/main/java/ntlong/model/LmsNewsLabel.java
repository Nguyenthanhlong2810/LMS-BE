package ntlong.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class LmsNewsLabel extends BaseEntity {
    @Column(name = "label")
    @Size(max = 20, message = "The length of label can not exceed 20 characters")
    private String label;

    public LmsNewsLabel(String label) {
        this.label = label;
    }

    public LmsNewsLabel() {

    }
}
