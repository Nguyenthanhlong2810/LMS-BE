package ntlong.payload.response.lesson;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonProcessResponse {
    private Long assignCourseId;

    private Long lessonStructureId;

    private Boolean isCompleted;
}
