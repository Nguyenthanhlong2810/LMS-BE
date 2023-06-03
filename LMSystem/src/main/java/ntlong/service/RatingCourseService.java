package ntlong.service;

import ntlong.dto.RatingDTO;
import ntlong.payload.request.RatingCourseRequest;
import ntlong.payload.response.RatingResponse;
import org.springframework.data.domain.Page;

public interface RatingCourseService {

    void createRatingCourse(RatingCourseRequest ratingCourseRequest, String username);

    RatingResponse getRatingCourse(Long courseId);

    Page<RatingDTO> getRatings(int pageNo, int pageSize, Long courseId, int rate, String sortBy);
}
