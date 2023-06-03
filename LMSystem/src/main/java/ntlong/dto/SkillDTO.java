package ntlong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SkillDTO {
    private String name;

    public SkillDTO(String name) {
        this.name = name;
    }
}
