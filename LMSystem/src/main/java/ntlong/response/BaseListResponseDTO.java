package ntlong.response;

import ntlong.model.Course;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseListResponseDTO {
    @ApiModelProperty()
    Long total;
    @ApiModelProperty(position = 1)
    List<Course> list;
}
