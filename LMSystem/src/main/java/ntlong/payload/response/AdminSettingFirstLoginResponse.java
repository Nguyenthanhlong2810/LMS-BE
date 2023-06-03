package ntlong.payload.response;

import ntlong.model.Experience;
import ntlong.model.Skill;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AdminSettingFirstLoginResponse {
    private List<Skill> skills;
    private List<Experience> experiences;
    private boolean skip;
}
