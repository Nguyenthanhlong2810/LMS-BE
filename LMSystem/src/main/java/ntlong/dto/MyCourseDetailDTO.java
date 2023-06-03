package ntlong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyCourseDetailDTO {

    private Long id; // ID của khóa học
    private String courseName; // Tên khóa học
    private String summary; // Tổng quan của khóa học
    private List<LessonStructureDTO> lessonStructures; // Cấu trúc khóa học
    private String fileNamePreview; // Tên ảnh/video preview của khóa học
    private String pathPreview; // Link ảnh/video preview của khóa học
    private int totalCourseDuration; // Tổng thời lượng khóa học -> số phút
    private int lessonTotal; // Tổng số bài học trong khóa học
    private int totalLessonDetailOfCourse; // Tổng số tiết học của khóa học
    private boolean completedByOrder;//Hoan thanh tuan tu
    private float rate;
}
