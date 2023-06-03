package ntlong.payload.response.course;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgressUserCourseResponse {

    private int totalLessonOfCourse;

    private int totalCompletedLessonOfCourse;

    private boolean isCompleted;

    private int totalContentUpload;

    private int totalCompletedContentUpload;
}
