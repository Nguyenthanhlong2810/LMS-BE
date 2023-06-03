package ntlong.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ntlong.dto.LessonContentUploadDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseSettingRequest {

    private Long courseId;

    private Long id;

    private Boolean isActivated;

    private Boolean isCertificated;

    private Boolean completedByOrder;

    private List<LessonContentUploadDTO> lessonContentUploads;


}
