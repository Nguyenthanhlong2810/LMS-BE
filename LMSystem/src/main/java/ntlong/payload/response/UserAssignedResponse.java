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
public class UserAssignedResponse extends BaseDTO {
    private String fullname;
    private String code;
    private String email;
    private String department;
    private String position;
    private String createdBy;
}
