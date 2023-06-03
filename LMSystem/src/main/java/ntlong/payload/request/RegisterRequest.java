package ntlong.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntlong.exception.validation.EmailRegex;
import ntlong.model.AppUserRole;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @EmailRegex
    private String email;

    private String fullname;

    private String username;

    private String password;

    private String confirmPassword;

    List<AppUserRole> appUserRoles;
}
