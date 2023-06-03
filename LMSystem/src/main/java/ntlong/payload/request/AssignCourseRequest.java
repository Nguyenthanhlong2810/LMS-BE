package ntlong.payload.request;

import ntlong.dto.BaseDTO;
import ntlong.enums.TypeAssignEnum;
import ntlong.enums.UnitTimeEnum;
import ntlong.payload.response.UserAssignResponse;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignCourseRequest extends BaseDTO {

    private Long courseId; // Khóa học

    private String nameCourse;

    @NotEmpty(message = "Học viên là bắt buộc")
    private List<Long> appUserIds; // Học viên

    private List<UserAssignResponse> appUsers; // Học viên

    @NotNull(message = "Loại hình gán là bắt buộc")
    private TypeAssignEnum typeAssign; // Loại hình gán

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date assignDate; //Ngày gán

    @NotNull(message = "Thời lượng là bắt buộc")
    private int time; // Thời  lượng

    @NotNull(message = "Đơn vị thời lượng là bắt buộc")
    private UnitTimeEnum unitTime; // Đơn vị

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fromTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date toTime;

    private UnitTimeEnum unitTrainTime;

    private MultipartFile file;
}