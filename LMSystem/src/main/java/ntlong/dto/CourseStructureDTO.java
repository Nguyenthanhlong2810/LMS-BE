package ntlong.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class CourseStructureDTO {
    private Long id;
    private String courseName;
    private boolean completedByOrder;
    private List<LessonStructureDTO> lessonStructures;
}
