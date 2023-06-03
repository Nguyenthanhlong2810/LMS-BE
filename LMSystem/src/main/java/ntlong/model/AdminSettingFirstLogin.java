package ntlong.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admin_setting_first_login")
public class AdminSettingFirstLogin extends BaseEntity {

    @Column(name = "is_skip", nullable = false)
    private boolean isSkip;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "admin_experiences",
            joinColumns = {
                    @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private List<Experience> experiences;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "admin_skills",
            joinColumns = {
                    @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private List<Skill> skills;
}
