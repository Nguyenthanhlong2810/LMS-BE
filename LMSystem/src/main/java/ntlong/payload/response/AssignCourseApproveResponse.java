package ntlong.payload.response;

import ntlong.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AssignCourseApproveResponse extends BaseDTO {
    private AssignDTOResponse assignCourse;
    private String nameApprover;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateApprove;
    private String statusApprove;
    private String reasonRegistry;//Lý do đăng ký
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate duedateRegistry;// Hạn đăng ký
    private String reasonReject; // Lý do từ chối
    private String reasonReturn; // Lý do gửi trả
    private String positionApprove;//Vị trí approve
}
