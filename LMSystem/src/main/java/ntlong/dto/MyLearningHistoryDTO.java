package ntlong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyLearningHistoryDTO {
    private Long id;
    private Long contentId;
    private Long lessonStructureId;
    private String type;
    private boolean isCompleted;
    private Long score; // Áp dụng đối với bài kiểm tra
}
