package ntlong.response;

import lombok.*;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorDetailsResponse {
    private Date timestamp;
    private String message;
    private String details;
}
