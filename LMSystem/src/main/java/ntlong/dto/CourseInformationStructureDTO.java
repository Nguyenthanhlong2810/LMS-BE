package ntlong.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class CourseInformationStructureDTO {
    private Long id;
    private String courseName;
    private boolean completedByOrder;
    private List<LessonStructureInformationDTO> lessonStructures;
    private Long totalDuration;
    private Long lessonCount;
    private String totalDurationDisplay;
}
