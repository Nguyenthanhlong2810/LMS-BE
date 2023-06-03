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
public class UserApproveResponse extends BaseDTO {
    private String username;
    private String fullname;
    private String position;
    private String department;
}
