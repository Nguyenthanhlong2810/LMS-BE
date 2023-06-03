package ntlong.payload.response.categorytraining;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryTrainingSearchResponse {
    private Long id;
    private String name;
    private long parent;
    private int no;
    private String language;
    private int totalCourse;
}
