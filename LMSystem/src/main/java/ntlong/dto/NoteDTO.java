package ntlong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NoteDTO {

    private Long id;

    private Long time;

    private String content;

    private Long contentUploadId;

    private Long lessonStructureId;

    private String lessonName;

    private Long lessonContentUploadId;

    public NoteDTO(Long id, Long time, String content, Long contentUploadId, Long lessonStructureId) {
        this.id = id;
        this.time = time;
        this.content = content;
        this.contentUploadId = contentUploadId;
        this.lessonStructureId = lessonStructureId;
    }
}
