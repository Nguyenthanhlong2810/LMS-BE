package ntlong.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@Data
public class AdminExperienceDTO
{
    @ApiModelProperty()
    private String username;
    @ApiModelProperty(position = 1)
    private String email;
    @ApiModelProperty(position = 2)
    private String language;
    @ApiModelProperty(position = 3)
    private Set<ExperienceDTO> experiences;
}
