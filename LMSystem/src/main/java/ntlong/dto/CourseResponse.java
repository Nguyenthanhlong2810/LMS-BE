package ntlong.dto;

import lombok.*;
import ntlong.model.CategoryTraining;

import java.util.List;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private Long id;
    private String name;
    private String categoryTrainingName;
    private String fileNameOfferBy;
    private String fileNameProvideBy;
    private List<String> tags;
    private List<String> skills;
    private String summary;
    private String detail;
    private String fileNamePreview;
    private String pathPreview;
    private List<String> requirementCourses;
    private List<String> experiences;
    private String instructorName;
    private Boolean freeCourse;
    private boolean isHot;
    private Long price;
    private Boolean activated;
    private String createdDate;
    private String lastUpdated;
    private float rate;
    private int numRate;

}
