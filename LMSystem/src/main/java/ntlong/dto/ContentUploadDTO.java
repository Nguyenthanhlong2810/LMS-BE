package ntlong.dto;

import ntlong.enums.TypeContentUploadEnum;
import lombok.*;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContentUploadDTO {

    private Long id;

    private Long lessonContentUploadId;

    private String nameContent; //Tên nội dung

    @NotNull(message = "Loại nội dung là bắt buộc")
    private TypeContentUploadEnum type; //Loại nội dung

    @NotNull(message = "Thời lượng là bắt buộc")
    private String timeLong; //Thời lượng

    private boolean canDownload;

    private boolean completedOpen;

    private String linkFileContent; //Link file nội dung

    private long sortOrder;

    private long duration;

    private int conditionPass;

    private boolean isCompleted; // Trạng thái học viên hoàn thành nội dung

    private boolean statusUsed;
}
