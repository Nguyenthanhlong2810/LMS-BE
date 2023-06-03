package ntlong.repository;

import ntlong.dto.RatingDTO;
import ntlong.enums.RateStar;
import ntlong.model.RatingCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingCourseRepository extends JpaRepository<RatingCourse,Long> {
    @Query("select avg(rc.rateStar) from RatingCourse rc where rc.course.id = :courseId")
    float getRatingCourse(Long courseId);

    @Query("select count(rc.rateStar) from RatingCourse rc where rc.course.id = :courseId and rc.rateStar =:rateStar")
    int countRatingByStar(Long courseId,int rateStar);

    @Query("select count(rc.rateStar) from RatingCourse rc where rc.course.id = :courseId")
    int countRatingByStar(Long courseId);

    @Query("select new ntlong.dto.RatingDTO(rt.id,rt.content,rt.rateStar,rt.course.id,rt.appUser.id, au.avatarUrl, au.fullname) " +
            "from RatingCourse rt inner join rt.appUser au where " +
            "rt.deleted = false and rt.course.id = :courseId and (:rate = 0 or rt.rateStar = :rate)")
    Page<RatingDTO> getRatings(Pageable paging, Long courseId, int rate);
}
