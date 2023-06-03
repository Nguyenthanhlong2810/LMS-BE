package ntlong.repository;

import ntlong.dto.CourseDTO;
import ntlong.model.Course;
import ntlong.payload.response.CourseCategoryTrainingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("select c from Course c inner join c.courseSetting cs where c.deleted = false " +
            "and cs.isActivated = true and c.id in :ids")
    List<Course> findCoursesByIds(List<Long> ids);
    boolean existsByNameAndDeletedFalse(String name);

    @Query("select c from Course c where c.id = :id and c.deleted = false ")
    Course findByLongId(Long id);

    Optional<Course> findByIdAndDeletedFalse(Long id);

    @Query("select e from Course e left join e.courseSetting cs where  " +
            "e.deleted = false " +
            "and (:name is null or :name = '' or unaccent(lower(e.name)) like unaccent(lower(concat('%',trim(:name),'%')))) " +
            "and ((:isActivated is null) " +
            "or ((:isActivated is not null and :isActivated = true) and cs.isActivated = :isActivated)" +
            " or ((:isActivated is not null and :isActivated = false) and (cs.isActivated is null or cs.isActivated = :isActivated)))" +
            " order by e.id desc")
    Page<Course> searchCourseList(@Param("name") String name,
                                  @Param("isActivated") Boolean isActivated,
                                  Pageable pageable);
    @Transactional
    @Modifying
    @Query(value = "update Course e set e.deleted = true where e.id =:id")
    void deleteCourse(Long id);

    @Query("select e from Course e inner join e.courseSetting cs where " +
            "(unaccent(lower(e.name)) like unaccent(lower(concat('%', :name ,'%'))) or :name is null or :name='') " +
            "and e.deleted = false and cs.isActivated = true "
    )
    List<Course> getListCourseFilter(@Param("name") String name);



    @Query(value = "select count(s) from Course c join c.skills s inner join c.courseSetting cs where s.id = :skill_id" +
            " and cs.isActivated = true and c.deleted = false ")
    int getTotalCourseHasSkill(Long skill_id);

    @Query(value = "select count(c) from Course c inner join c.courseSetting cs inner join c.experiences e where e.name = :experienceName" +
            " and cs.isActivated = true and c.deleted = false ")
    int getTotalCourseHasExperience(String experienceName);

    @Query(value = "select count(c) from Course c inner join c.courseSetting cs inner join c.categoryTraining ct where ct.name = :categoryTrainingName" +
            " and cs.isActivated = true and c.deleted = false ")
    int getTotalCourseHasCategoryTraining(String categoryTrainingName);

    @Query(value = "select count(cu.id) from LessonContentUpload lcu inner join lcu.contentUpload cu " +
            "inner join lcu.lessonStructure ls inner join ls.course c where cu.type = 'VIDEO' and c.id = :courseId")
    int getTotalContentUpload(@Param("courseId") Long courseId);

    @Query("select case when ( count(c) > 0) then true else false end from Course c inner join c.categoryTraining ct " +
            "where c.deleted = false and ct.name = :categoryName")
    boolean existCategoryNameInCourse(@Param("categoryName") String categoryName);


    @Query("select new ntlong.payload.response.CourseCategoryTrainingResponse(c.id,c.pathPreview," +
            "c.name,c.instructorName,c.rate,c.price) " +
            "from Course c inner join c.courseSetting cs inner join c.categoryTraining ct " +
            "where c.deleted = false and cs.isActivated = true and ct.id = :categoryTrainingID")
    List<CourseCategoryTrainingResponse> getCourseByCategoryTraining(Long categoryTrainingID);

    @Query("select new ntlong.dto.CourseDTO(c.id,c.name,c.categoryTraining.name,c.pathPreview,c.freeCourse,c.price,c.isHot,c.rate) from Course c inner join c.courseSetting cs where c.deleted = false and cs.isActivated = true " +
            "order by c.rate desc ")
    Page<CourseDTO> findHighRatingCourses(Pageable pageable);
}
