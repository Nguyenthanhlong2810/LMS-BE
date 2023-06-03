package ntlong.repository;

import ntlong.model.UserLessonProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface UserLessonProcessRepository extends JpaRepository<UserLessonProcess,Long> {
    boolean existsUserLessonProcessByAssignCourse_IdAndLessonStructure_Id(Long assignCourseId, Long lessonStructureId);

    UserLessonProcess getUserLessonProcessByAssignCourse_IdAndLessonStructure_Id(Long assignCourseId, Long lessonStructureId);

    boolean existsUserLessonProcessByAssignCourseIdAndLessonStructureIdAndIsCompletedTrue(Long assignCourseId, Long lessonStructureId);

    @Modifying
    void deleteAllByAssignCourseId(Long assignCourseId);
}
