package ntlong.payload.request;

import java.util.List;
import java.util.Set;

import ntlong.model.Skill;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntlong.model.AppUserRole;

@Data
@NoArgsConstructor
public class LoginRequest {
    @ApiModelProperty()
    private String username;
    @ApiModelProperty(position = 1)
    private String email;
    @ApiModelProperty(position = 2)
    private String password;
    @ApiModelProperty(position = 3)
    List<AppUserRole> appUserRoles;
    @ApiModelProperty(position = 5)
    private Set<Skill> appSkills;
    @ApiModelProperty(position = 6)
    private String learningPath;
}
