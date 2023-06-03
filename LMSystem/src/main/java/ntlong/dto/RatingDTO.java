package ntlong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {

    private Long id;

    private String content;

    private int rateStar;

    private Long courseId;

    private Long appUserId;

    private String avtUser;

    private String fullName;
}
