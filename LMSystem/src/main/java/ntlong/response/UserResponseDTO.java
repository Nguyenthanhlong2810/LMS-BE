package ntlong.response;

import ntlong.model.AppUserRole;
import ntlong.model.Experience;
import ntlong.model.Skill;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserResponseDTO {
    @ApiModelProperty()
    private Integer id;
    @ApiModelProperty(position = 1)
    private String username;
    @ApiModelProperty(position = 2)
    private String email;
    @ApiModelProperty(position = 3)
    List<AppUserRole> appUserRoles;
    @ApiModelProperty(position = 4)
    private Set<Skill> skillsInteresting;
    @ApiModelProperty(position = 5)
    private String learningPath;
    @ApiModelProperty(position = 6)
    private List<Experience> experiences;

    @ApiModelProperty(position = 7)
    private boolean firstLoginSetup;

    @ApiModelProperty(position = 8)
    private String fullname;

}
