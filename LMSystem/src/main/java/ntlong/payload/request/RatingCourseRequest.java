package ntlong.payload.request;

import lombok.Data;

@Data
public class RatingCourseRequest {

    private int rateStar;

    private String content;

    private Long courseId;
}
