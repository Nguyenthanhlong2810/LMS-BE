package ntlong.service.impl;

import javassist.tools.rmi.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import ntlong.dto.NoteDTO;
import ntlong.dto.RatingDTO;
import ntlong.enums.RateStar;
import ntlong.exception.CustomException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.AppUser;
import ntlong.model.Course;
import ntlong.model.LessonContentUpload;
import ntlong.model.RatingCourse;
import ntlong.payload.request.RatingCourseRequest;
import ntlong.payload.response.RatingResponse;
import ntlong.repository.CourseRepository;
import ntlong.repository.RatingCourseRepository;
import ntlong.repository.UserRepository;
import ntlong.service.RatingCourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RatingCourseServiceImpl implements RatingCourseService {

    private final RatingCourseRepository ratingRepository;

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;
    @Override
    @Transactional
    public void createRatingCourse(RatingCourseRequest ratingCourseRequest, String username) {
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        if(Objects.isNull(appUser)){
            throw new CustomException("Người dùng không tồn tại", HttpStatus.BAD_REQUEST);
        }
        Course course = courseRepository.findByIdAndDeletedFalse(ratingCourseRequest.getCourseId())
                .orElseThrow(() -> new CustomException("Khóa học không tồn tại", HttpStatus.BAD_REQUEST));
        RatingCourse ratingCourse = new RatingCourse();
        ratingCourse.setRateStar(ratingCourseRequest.getRateStar());
        ratingCourse.setContent(ratingCourseRequest.getContent());
        ratingCourse.setCourse(course);
        ratingCourse.setAppUser(appUser);

        ratingRepository.save(ratingCourse);
        float avgRate = ratingRepository.getRatingCourse(course.getId());
        course.setRate(Float.parseFloat(String.format("%.1f", avgRate)));

        courseRepository.save(course);

    }

    @Override
    public RatingResponse getRatingCourse(Long courseId) {
        int totalRate = ratingRepository.countRatingByStar(courseId);
        Course course = courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new CustomException("Khóa học không tồn tại", HttpStatus.BAD_REQUEST));
        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setRate(course.getRate());

        ratingResponse.setRateFiveStar(calculatePercentStar(ratingRepository.countRatingByStar(courseId,5),totalRate));
        ratingResponse.setRateFourStar(calculatePercentStar(ratingRepository.countRatingByStar(courseId,4),totalRate));
        ratingResponse.setRateThreeStar(calculatePercentStar(ratingRepository.countRatingByStar(courseId,3),totalRate));
        ratingResponse.setRateTwoStar(calculatePercentStar(ratingRepository.countRatingByStar(courseId,2),totalRate));
        ratingResponse.setRateOneStar(calculatePercentStar(ratingRepository.countRatingByStar(courseId,1),totalRate));

        return ratingResponse;
    }

    @Override
    public Page<RatingDTO> getRatings(int pageNo, int pageSize, Long courseId, int rate, String sortBy) {
        Pageable paging = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Order.desc(sortBy)));
        Page<RatingDTO> ratingDTOS = ratingRepository.getRatings(paging,courseId,rate);
        return ratingDTOS;
    }

    private int calculatePercentStar(int totalStar, int totalRate){
       return (int) (((float) totalStar / totalRate) * 100);
    }
}
