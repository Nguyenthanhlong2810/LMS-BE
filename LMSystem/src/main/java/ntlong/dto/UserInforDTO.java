package ntlong.dto;

import ntlong.model.AppUserRole;
import ntlong.model.Experience;
import ntlong.model.Skill;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserInforDTO {
    private long id;
    private String username;
    private String email;
    private List<AppUserRole> appUserRoles;
    private Set<Skill> skillsInteresting;
    private Set<Experience> experiences;
    private String learningPath;
    private String skip;
    private String fullname;
    private String division;
    private String department;
    private String birthdate;
    private String phoneNumber;
    private String site;
    private String position;
    private String managerName;
    private String managerEmail;
    private String avatarUrl;
    private String facebook;
    private String desiredLearningPath;
    private boolean firstLoginSetup;
}
