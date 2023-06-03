package ntlong.repository;

import ntlong.dto.NoteDTO;
import ntlong.model.NoteLesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NoteLessonRepository extends JpaRepository<NoteLesson, Long> {
    @Query(value = "select new ntlong.dto.NoteDTO(nl.id,nl.time,nl.content,nl.contentUpload.id,nl.lessonStructure.id) " +
            "from NoteLesson nl where nl.contentUpload.id = :contentUploadId " +
            "and nl.appUser.id = :userId and nl.lessonStructure.id = :lessonStructureId")
    Page<NoteDTO> getNoteListByContentUpload(Pageable paging, @Param("contentUploadId") Long contentUploadId,
                                             @Param("lessonStructureId") Long lessonStructureId,
                                             @Param("userId") Long userId);
//
//    @Query(value = "select new ntlong.dto.NoteDTO(nl.id,nl.time,nl.content,nl.contentUpload.id,nl.lessonStructure.id) " +
//            "from NoteLesson nl inner join ContentUpload cu on nl.contentUpload.id = cu.id " +
//            "inner join LessonContentUpload lcu on cu.id = lcu.contentUpload.id inner join LessonStructure ls " +
//            "on lcu.lessonStructure.id = ls.id inner join Course c on ls.course.id = c.id " +
//            "where c.id = :courseId and nl.appUser.id = :userId")
//    Page<NoteDTO> getNoteListByCourse(Pageable paging, @Param("courseId") Long courseId, @Param("userId") Long userId);


//    @Query(value = "select new ntlong.dto.NoteDTO(nl.id,nl.time,nl.content,nl.contentUpload.id,nl.lessonStructure.id) " +
//            "from Course c inner join c.lessonStructures ls inner join ls.lessonContentUploads lcu inner join lcu.contentUpload cu " +
//            "inner join NoteLesson nl on nl.lessonStructure.id = ls.id "+
//            "where c.id = :courseId and nl.appUser.id = :userId")
//    Page<NoteDTO> getNoteListByCourse(Pageable paging, @Param("courseId") Long courseId, @Param("userId") Long userId);

    @Query(value = "select new ntlong.dto.NoteDTO(nl.id,nl.time,nl.content,nl.contentUpload.id,nl.lessonStructure.id) " +
            "from NoteLesson nl inner join nl.lessonStructure ls inner join ls.course c inner join AssignCourse ac on c.id = ac.course.id "+
            "where c.id = :courseId and nl.appUser.id = :userId")
    Page<NoteDTO> getNoteListByCourse(Pageable paging, @Param("courseId") Long courseId, @Param("userId") Long userId);


    @Modifying
    @Transactional
    @Query("delete from NoteLesson nl where nl.contentUpload.id in (:ids)")
    void deleteAllByContentUploadId(@Param("ids") List<Long> ids);
}
