package ntlong.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserExtraInfomation {
    private String userId;
    private String ldap;
    private String division;
    private String department;
    private String location;
    private String gender;
    private String jobLevel;
    private String positionTitle;
    private String businessEmail;
    private String workingLocation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date terminationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date positionEffectiveDate;
    private String fullName;
    private String managerFullName;
    private String managerEmail;
}
