package ntlong.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseCategoryTrainingResponse {

    private Long id;

    private String previewUrl;

    private String title;

    private String instructor;

    private float rate;

    private long price;
}
