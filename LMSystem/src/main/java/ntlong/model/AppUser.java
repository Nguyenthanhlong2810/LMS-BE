package ntlong.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data // Create getters and setters
@NoArgsConstructor
public class AppUser extends BaseEntity {

    @Size(min = 4, max = 255, message = "Minimum username length: 4 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @Email
    @Column(name = "email")
    private String email;

//    @Size(min = 8, message = "Minimum password length: 8 characters")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<AppUserRole> appUserRoles;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(joinColumns = {
            @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private Set<Skill> skillsInteresting;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(joinColumns = {
            @JoinColumn(nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(nullable = false, updatable = false)})
    private Set<Experience> experiences;

    @Size(max = 4000)
    @Column
    private String learningPath;

    @Column
    private String fullname;

    private String birthdate;

    @Length(min = 9, max = 13, message = "Phone number length only from 9 to 13 characters")
    private String phoneNumber;

    private String avatarUrl;

    @Length(max = 150, message = "The length can not exceed 150 characters")
    private String facebook;

    @Column(name = "first_login_setup", columnDefinition = "boolean default false")
    private boolean firstLoginSetup;

    @Column(name = "enabled", columnDefinition = "boolean default false")
    private boolean enabled;

    @Column
    private String site;

    @Column
    private String position;
}
