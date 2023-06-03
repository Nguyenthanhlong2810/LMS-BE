package ntlong.dto.lessonprocess;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserLessonProcessDTO {

    private Long assignCourseId;

    private Long lessonStructureId;

    private Boolean isCompleted;
}
