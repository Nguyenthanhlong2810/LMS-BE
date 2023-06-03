package ntlong.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class AdminSettingFirstLoginDTO {
    private Set<String> experiences;
    private Set<String> skills;
    private boolean skip;
}
