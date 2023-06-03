package ntlong.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CourseAssignedResponse{

    private Long courseId;

    private String nameCourse;

    private String codeCourse;

    private String description;

    private String createdBy;

    public CourseAssignedResponse(Long courseId, String nameCourse, String codeCourse, String description) {
        this.courseId = courseId;
        this.nameCourse = nameCourse;
        this.codeCourse = codeCourse;
        this.description = description;
    }
}
