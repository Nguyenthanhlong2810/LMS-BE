package ntlong.dto;

import ntlong.enums.StatusCourseEnum;
import lombok.*;
import ntlong.enums.TypeAssignEnum;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseHistoryDTO {
    private Long courseId;
    private String name;
    private String pathPreviewIMG;
    private String categoryTrainingName;
    private String summary;
    private String detail;
    //private String skills;
    private Date dueDate;
    private Date startedDate;
    private String instructorName;

    private String courseProgress;

    private Date lastUpdate;

    private int remainingTime;

    private Date completedDate;

    private StatusCourseEnum statusCourse;

    private TypeAssignEnum typeAssign;

    public CourseHistoryDTO(Long courseId, String name,
                            String pathPreviewIMG, String categoryTrainingName,
                            String summary,
                            String detail,
                            Date dueDate, Date startedDate,
                            String instructorName,
                            Date lastUpdate,
                            Date completedDate,
                            StatusCourseEnum statusCourse,
                            TypeAssignEnum typeAssign) {
        this.courseId = courseId;
        this.name = name;
        this.pathPreviewIMG = pathPreviewIMG;
        this.categoryTrainingName = categoryTrainingName;
        this.summary = summary;
        this.detail = detail;
        this.dueDate = dueDate;
        this.startedDate = startedDate;
        this.instructorName = instructorName;
        this.lastUpdate = lastUpdate;
        this.completedDate = completedDate;
        this.statusCourse = statusCourse;
        this.typeAssign = typeAssign;
    }
}
