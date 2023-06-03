package ntlong.payload.response.course;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProcessUserCourseResponse {
    private int totalCourse;

    private int completedCourse;

    private int uncompletedCourse;

    private int notStartedCourse;

    private int totalCertifications;

    private int totalAwardedCertifications;
}
