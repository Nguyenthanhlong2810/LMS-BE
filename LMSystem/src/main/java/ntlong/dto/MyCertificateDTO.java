package ntlong.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyCertificateDTO {

    private Long assignCourseId;
    private Long courseId; // ID khóa học học viên được assign
    private Long appUserId; // ID của học viên
    private String courseName; // Tên (tiêu đề) khóa học
    private String coursePathPreview; // Link file thumbnail của khóa học
    private String courseFileNamePreview; // Tên file thumbnail của khóa học
    private String courseSummary; // Tổng quan của khóa học
    private long courseDuration; // Thời lượng khóa học
    private long courseContentTotal; // Tổng số bài giảng
    private String userFullName; // Tên của học viên
    private String userAvatarUrl; // URL ảnh của học viên
    private String courseType = "COURSE";
    private String myCertificateImageLink;
    private String myCertificatePdfLink;

    private Date completedDate;

    private boolean isCertification;

    private String username;

    public MyCertificateDTO(Long assignCourseId
            , Long courseId
            , Long appUserId
            , String courseName
            , String coursePathPreview
            , String courseFileNamePreview
            , String courseSummary
            , String userFullName
            , String userAvatarUrl
            , String myCertificateImageLink
            , String myCertificatePdfLink
                            ,String username
            , Date completedDate) {
        this.assignCourseId = assignCourseId;
        this.courseId = courseId;
        this.appUserId = appUserId;
        this.courseName = courseName;
        this.coursePathPreview = coursePathPreview;
        this.courseFileNamePreview = courseFileNamePreview;
        this.courseSummary = courseSummary;
        this.userFullName = userFullName;
        this.userAvatarUrl = userAvatarUrl;
        this.myCertificateImageLink = myCertificateImageLink;
        this.myCertificatePdfLink = myCertificatePdfLink;
        this.username = username;
        this.completedDate = completedDate;
    }

    public MyCertificateDTO(String courseName,
            String username
            , String userFullName) {
        this.courseName = courseName;
        this.userFullName = userFullName;
        this.username = username;
    }
}
