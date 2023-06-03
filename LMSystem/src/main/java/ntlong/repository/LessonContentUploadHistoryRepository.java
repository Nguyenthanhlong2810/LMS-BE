package ntlong.repository;

import ntlong.model.LessonContentUploadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LessonContentUploadHistoryRepository extends JpaRepository<LessonContentUploadHistory, Long> {
    boolean existsByLessonContentUpload_IdAndAppUser_IdAndIsCompletedTrue(Long lessonContentUploadId, Long appUserId);
    boolean existsByAppUser_IdAndLessonContentUpload_Id(Long appUserId, Long lessonContentUploadId);
    LessonContentUploadHistory findByAppUser_IdAndLessonContentUpload_Id(Long appUserId, Long lessonContentUploadId);

    @Modifying
    @Transactional
    @Query("delete from LessonContentUploadHistory nl where nl.lessonContentUpload.id in (:ids)")
    void deleteAllByLessonContentUploadId(@Param("ids") List<Long> ids);

    @Query(value = "select count(lcuh.id) from LessonContentUploadHistory lcuh inner join lcuh.lessonContentUpload lcu " +
            "inner join lcu.lessonStructure ls inner join ls.course c inner join lcu.contentUpload cu " +
            "where cu.type = 'VIDEO' and c.id = :courseId and lcuh.appUser.id = :userId" +
            " and lcuh.isCompleted = true")
    int getTotalCompletedContentUpload(@Param("courseId") Long courseId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("delete from LessonContentUploadHistory lcuh where lcuh.appUser.id = :userId " +
            "and lcuh.lessonContentUpload.id in (:lessonContentUploadIds)")
    void deleteAllByAppUserIdAndLessonContentUploadIds(@Param("userId") Long userId,
                                                       @Param("lessonContentUploadIds") List<Long> lessonContentUploadIds);
}
