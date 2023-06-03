package ntlong.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"system", "image"})
public class UserLdap {
    private String id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String phone;
    private String dateOfBirth;
    private String externalInfo;
    private String errorMessage;
    private boolean status;
    private String createdDate;
    private String updatedDate;
    private String createdBy;
    private String updatedBy;
}