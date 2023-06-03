package ntlong.dto;

import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.enums.UnitTimeEnum;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignCourseDTO {

    private Long id;

    private Long courseId; // Khóa học

    private String nameCourse;

    private String nameUser;

    @NotEmpty(message = "Học viên là bắt buộc")
    private Long appUserId; // Học viên

    private String nameAppUser;

    @NotNull(message = "Loại hình gán là bắt buộc")
    private TypeAssignEnum typeAssign; // Loại hình gán

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date assignDate; //Ngày gán

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fromTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date toTime;

    private StatusCourseEnum progressStatus;
}
