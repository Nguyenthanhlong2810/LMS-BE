package ntlong.dto;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SettingLessonDTO extends BaseDTO {

    private String nameContent;

    private Set<ContentUploadDTO> contentUploadDTOs;

    private String type;
}
