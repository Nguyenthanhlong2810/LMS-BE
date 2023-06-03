package ntlong.dto.delete;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class ListIdDTO {
    private List<Long> ids;
}
