package ntlong.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Setter
@Getter
public class CategoryTraining extends BaseEntity {

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "title", length = 200)
    private String title;

    private long parent; //categoryId

    private int no;
}
