package ntlong.repository;

import ntlong.model.LessonContentUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonContentUploadRepository extends JpaRepository<LessonContentUpload, Long> {
    List<LessonContentUpload> findLessonContentUploadsByLessonStructureId(Long lessonStructureId);

    List<LessonContentUpload> findLessonContentUploadsByLessonStructureIdIn(List<Long> lessonStructureIds);
    void deleteAllByLessonStructureId(Long lessonStructureId);
    List<LessonContentUpload> findLessonContentUploadsByLessonStructureIdAndContentUploadId(Long lessonStructureId,Long contentUploadId);

    List<LessonContentUpload> findLessonContentUploadsByContentUploadId(Long contentUploadId);

    @Query("select lcu.duration from LessonContentUpload lcu where lcu.lessonStructure.id = :id")
    List<Long> findAllDurationByLessonStructureId(@Param("id") Long id);

    @Query("Select distinct lcu.contentUpload.id from LessonContentUpload lcu")
    List<Long> findContentUploadIdList();

    LessonContentUpload findLessonContentUploadByLessonStructureIdAndContentUploadId(Long lessonStructureId,Long contentUploadId);

    @Query("select lcu.id from LessonContentUpload lcu inner join lcu.lessonStructure ls inner join " +
            " ls.course c where c.id = :courseId")
    List<Long> getListLessonContentUploadIdByCourseId(@Param("courseId") Long courseId);
}
