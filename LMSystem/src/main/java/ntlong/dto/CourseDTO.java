package ntlong.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
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
    private Long price;
    private Boolean isHot;
    private float rate;

    public CourseDTO(Long id, String name, String categoryTrainingName,String pathPreview, Boolean freeCourse, Long price, Boolean isHot, float rate){
        this.id = id;
        this.name = name;
        this.categoryTrainingName = categoryTrainingName;
        this.pathPreview = pathPreview;
        this.freeCourse = freeCourse;
        this.price = price;
        this.isHot = isHot;
        this.rate = rate;
    }
}
