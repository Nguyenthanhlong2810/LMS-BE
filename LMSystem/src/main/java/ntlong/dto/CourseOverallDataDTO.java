package ntlong.dto;

import ntlong.enums.StatusCourseEnum;
import ntlong.model.Course;
import ntlong.model.RequirementCourse;
import ntlong.model.Tag;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseOverallDataDTO {

    private String courseName;

    private Set<RequirementCourse> courseRequirement;
    private String courseDescription;

    private Set<Tag> tagList;

    private String instructorName;

    private String videoUrl;

    private Long totalDuration;

    private Long downloadableCount;

    private Boolean isCertificated;

    private Set<RequirementCourse> requirementCourses;

    private String summary;


    private boolean isRequireApproval;

    private boolean isFree;

    private boolean isAssigned;

    private StatusCourseEnum statusCourse;

    private Boolean isOfferedBy;

    private String providedPath;

    public CourseOverallDataDTO(String courseName, Set<RequirementCourse> courseRequirement, String courseDescription, String instructorName, String videoUrl,String summary) {
        this.courseName = courseName;
        this.courseRequirement = courseRequirement;
        this.courseDescription = courseDescription;
//        this.tagList = tagList;
        this.instructorName = instructorName;
        this.videoUrl = videoUrl;
        this.summary=summary;
    }

    public static CourseOverallDataDTO fromEntity(Course entity) {
        if (entity == null) {
            return null;
        }
        return CourseOverallDataDTO.builder()
                .courseName(entity.getName())
                .courseRequirement(entity.getRequirementCourses())
                .courseDescription(entity.getDescription())
                .tagList(entity.getTags())
                .videoUrl(entity.getFileNamePreview())
                .instructorName(entity.getInstructorName())
                .build();
    }
}
