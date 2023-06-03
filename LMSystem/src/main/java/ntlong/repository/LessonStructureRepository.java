package ntlong.repository;

import ntlong.model.LessonStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonStructureRepository extends JpaRepository<LessonStructure, Long> {
    List<LessonStructure> findByCourseId(Long courseId);

    boolean existsByCourseId(Long courseId);

    @Query("select count( distinct ls.id) from LessonStructure ls inner join UserLessonProcess ulp on ls.id= ulp.lessonStructure.id " +
            " inner join AssignCourse ac on ulp.assignCourse.id = ac.id" +
            " where ac.course.id = :courseId and ac.appUser.id = :userId and ulp.isCompleted = :isCompleted")
    Integer getTotalCompletedLessonByCourseId (Long courseId, Long userId, Boolean isCompleted);

    @Query("select count(ls) from LessonStructure ls where ls.course.id =:courseId")
    Integer getTotalLessonByCourseId (Long courseId);

    @Query(value = "select ls.nameContent from LessonStructure ls inner join LessonContentUpload lcu on ls.id = lcu.lessonStructure.id " +
            "inner join ContentUpload cu on lcu.contentUpload.id = cu.id where cu.id = :contentUploadId and ls.id = :lessonStructureId")
    String getLessonStructureNameByContentUploadIdAndLessonStructureId(@Param("contentUploadId") Long contentUploadId,
                                      @Param("lessonStructureId") Long lessonStructureId);
}
