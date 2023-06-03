package ntlong.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SortCourseStructureRequest {
    private Long parentId;
    private List<IdValuePair> childIds;
    private String type;
}
