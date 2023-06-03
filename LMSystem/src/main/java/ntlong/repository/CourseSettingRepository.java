package ntlong.repository;

import ntlong.model.CourseSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseSettingRepository extends JpaRepository<CourseSetting, Long> {

    @Query(value = "select cs from CourseSetting cs join Course c on cs.course.id = c.id where c.id = :id")
    Optional<CourseSetting> getSettingCourseById(@Param("id") Long id);

    @Query("select cs from CourseSetting cs where cs.course.id = :courseId")
    CourseSetting findByCourseId(@Param("courseId") Long courseId);

    @Query("select cs from CourseSetting cs where cs.course.id = :courseId")
    List<CourseSetting> findAllByCourseId(@Param("courseId") Long courseId);
}
