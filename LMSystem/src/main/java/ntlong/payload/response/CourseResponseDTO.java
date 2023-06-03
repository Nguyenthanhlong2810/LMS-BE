package ntlong.payload.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CourseResponseDTO {
    private long id;
    private String name;
}
