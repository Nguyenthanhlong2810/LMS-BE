package ntlong.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LessonStructureDTO extends BaseDTO {

    @NotNull(message = "courseId is required")
    private Long courseId;

    @NotNull(message = "nameContent is required")
    private String nameContent;

    private List<Long> contentUploadIds;

    private List<ContentUploadDTO> contentUploads;

    private long sortOrder;

    private int totalLessonDetailOfLesson; // Tổng số tiết học của mỗi bài học

    private int totalLessonDetailOfLessonDuration; // Tổng thời lượng của mỗi bài học
}
