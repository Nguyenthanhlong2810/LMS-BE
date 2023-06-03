package ntlong.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LessonStructureInformationDTO {
    @NotNull(message = "courseId is required")
    private Long courseId;

    @NotNull(message = "nameContent is required")
    private String nameContent;

    private List<Long> contentUploadIds;

    private List<ContentUploadDTO> contentUploads;

    private long sortOrder;

    private long totalDuration;

    private String totalDurationDisplay;

    private long lessonCount;
}
