package ntlong.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseResponseDTO {
    @ApiModelProperty
    private String name;
    @ApiModelProperty(position = 1)
    private Long id;

    private String code;

    public BaseResponseDTO() {
    }

    public BaseResponseDTO(String name) {
        this.name = name;
    }

    public BaseResponseDTO(String name, Long id) {
        this.name = name;
        this.id = id;
    }
}
