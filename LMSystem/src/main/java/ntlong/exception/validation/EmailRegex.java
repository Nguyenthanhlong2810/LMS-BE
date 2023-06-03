package ntlong.exception.validation;


import ntlong.common.Constants;
import ntlong.exception.CustomException;
import org.springframework.http.HttpStatus;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.regex.Pattern;

@Documented
@Constraint(
        validatedBy = {EmailValidator.class}
)
@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})

@Retention(RetentionPolicy.RUNTIME)
public @interface EmailRegex {
    String message() default "Email not match regex";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
class EmailValidator implements ConstraintValidator<EmailRegex, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.trim().isEmpty()) {
            throw new CustomException("Email không được để trống", HttpStatus.BAD_REQUEST);
        } else {
            String regex = Constants.EMAIL_PATTERN;
            if (Pattern.matches(regex, value.trim())) {
                return true;
            } else {
                throw new CustomException("Email không hợp lệ",HttpStatus.BAD_REQUEST);
            }
        }
    }
}
