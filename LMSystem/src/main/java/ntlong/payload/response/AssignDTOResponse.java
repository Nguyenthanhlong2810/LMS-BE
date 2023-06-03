package ntlong.payload.response;

import ntlong.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AssignDTOResponse extends BaseDTO {

    private UserApproveResponse appUser;
    private CourseResponseDTO course;


}
