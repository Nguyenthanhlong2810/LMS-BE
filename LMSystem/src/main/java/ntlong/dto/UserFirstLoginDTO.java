package ntlong.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserFirstLoginDTO {
    private Set<String> skills;
    private Set<String> experiences;
    private String learningPath;
}
