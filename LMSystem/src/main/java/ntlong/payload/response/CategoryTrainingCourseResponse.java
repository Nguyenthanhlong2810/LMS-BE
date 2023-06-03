package ntlong.payload.response;


import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryTrainingCourseResponse {

    private Long id;

    private String name;

    private String title;

    private String description;

    private List<CourseCategoryTrainingResponse> data;
}
