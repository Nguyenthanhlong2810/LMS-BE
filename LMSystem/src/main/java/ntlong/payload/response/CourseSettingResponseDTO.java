package ntlong.payload.response;


import ntlong.model.Course;
import ntlong.model.CourseSetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSettingResponseDTO {

    private Long id;

    private Boolean isActivated;

    private Boolean isCertificated;

    private Boolean isCompleteRecorded;

    private Long courseId;

    private String name;

    public static CourseSettingResponseDTO fromEntity(CourseSetting entity) {
        if (entity == null) {
            return null;
        }
        return CourseSettingResponseDTO.builder()
                .id(entity.getId())
                .courseId(entity.getCourse().getId())
                .isActivated(entity.getIsActivated())
                .isCertificated(entity.getIsCertificated())
                .name(entity.getCourse().getName())
                .build();
    }

}
