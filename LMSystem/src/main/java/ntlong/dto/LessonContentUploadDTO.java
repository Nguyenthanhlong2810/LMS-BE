package ntlong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LessonContentUploadDTO {

    private long lessonStructureId;
    private String nameContentLesson;

    private long contentUploadId;
    private String nameContentUpload;

    private long sortOrder;

    private boolean canDownload;

    private boolean completedOpen;

    private int conditionPass;
}
