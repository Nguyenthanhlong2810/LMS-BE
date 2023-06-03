package ntlong.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminSettingDTO {
    @ApiModelProperty()
    private String username;
    @ApiModelProperty(position = 1)
    private String email;
    @ApiModelProperty(position = 2)
    private String language;
    @ApiModelProperty(position = 3)
    private Set<ExperienceDTO> experiences;
    @ApiModelProperty(position = 4)
    private Set<SkillDTO> skills;
    @ApiModelProperty(position = 5)
    private String skip;
}
