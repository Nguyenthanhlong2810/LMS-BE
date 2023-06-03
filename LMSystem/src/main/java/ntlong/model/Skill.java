package ntlong.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity @Data
public class Skill extends BaseEntity
{
    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "admin_created", columnDefinition = "boolean default true")
    private boolean adminCreated;
}
