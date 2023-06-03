package ntlong.response;

import ntlong.dto.ExperienceDTO;
import ntlong.dto.SkillDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CourseCategoryDTO {
    List<SkillDTO> skills;
    List<ExperienceDTO> experiences;

    public CourseCategoryDTO() {
    }
}
