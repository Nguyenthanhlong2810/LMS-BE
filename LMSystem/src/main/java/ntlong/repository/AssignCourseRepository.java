package ntlong.repository;

import ntlong.dto.AssignCourseDTO;
import ntlong.dto.MyCertificateDTO;
import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.model.AssignCourse;
import ntlong.payload.response.CourseAssignedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AssignCourseRepository extends JpaRepository<AssignCourse, Long> {
    List<AssignCourse> findAssignCoursesByCourseId(Long courseId);

    AssignCourse getAssignCourseByCourseIdAndAppUserIdAndDeletedFalse(Long courseId, Long appUserId);

    @Query("select distinct new ntlong.payload.response.CourseAssignedResponse(a.course.id,a.course.name,a.course.description,a.createdBy) " +
            "from AssignCourse a " +
            "where ( lower(a.course.name) like lower(concat('%',:courseName,'%')) or :courseName is null or :courseName='' ) " +
            "and ( lower(a.course.description) like lower(concat('%',:description,'%')) or :description is null or :description = '') ")
    Page<CourseAssignedResponse> getCourseAssigned(String courseName, String description, Pageable pageable);


    @Query("select a " +
            "from AssignCourse a " +
            "where (a.appUser.fullname like %:fullname% or :fullname is null or :fullname='' ) " +
            "order by a.lastUpdated desc ")
    Page<AssignCourse> getUserAssigned(String fullname, Pageable pageable);

    @Query("select new ntlong.dto.MyCertificateDTO ( ac.id, ac.course.id , ac.appUser.id " +
            " , c.name, c.pathPreview, c.fileNamePreview " +
            " , c.summary, au.fullname , au.avatarUrl, ac.myCertificateImageLink, " +
            "ac.myCertificatePdfLink, au.username, ac.completedDate) " +
            " from AssignCourse ac " +
            " inner join Course c on ac.course.id = c.id" +
            " inner join AppUser au on ac.appUser.id = au.id inner join c.courseSetting cs" +
            " where ac.deleted = false and ac.appUser.id =:appUserId and ac.progressStatus = 'COMPLETED'" +
            " and ( unaccent(lower(c.name)) like unaccent(concat('%', lower(trim(:keyword)) ,'%')) " +
            " or unaccent(lower(c.summary)) like unaccent(concat('%', lower(trim(:keyword)) ,'%'))" +
            " or :keyword is null " +
            " or :keyword = '') and cs.isActivated = true order by ac.completedDate desc")
    Page<MyCertificateDTO> findAllMyCertificates(@Param("appUserId") Long appUserId, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select new ntlong.dto.MyCertificateDTO ( ac.id, ac.course.id , ac.appUser.id " +
            " , c.name, c.pathPreview, c.fileNamePreview " +
            " , c.summary, au.fullname , au.avatarUrl, ac.myCertificateImageLink, ac.myCertificatePdfLink," +
            "au.username," +
            " ac.completedDate ) " +
            " from AssignCourse ac " +
            " inner join Course c on ac.course.id = c.id" +
            " inner join AppUser au on ac.appUser.id = au.id inner join c.courseSetting cs" +
            " where ac.appUser.id =:appUserId " +
            " and ac.course.id =:courseId " +
            " and ac.progressStatus = 'COMPLETED' and cs.isActivated = true")
    MyCertificateDTO findMyCertificateDetail(@Param("appUserId") Long appUserId, @Param("courseId") Long courseId);

    @Query(value = "select new ntlong.dto.MyCertificateDTO(c.name,au.username, au.fullname) " +
            " from AssignCourse ac " +
            " inner join Course c on ac.course.id = c.id inner join c.courseSetting cs " +
            " inner join AppUser au on ac.appUser.id = au.id " +
            " where ac.appUser.id =:appUserId " +
            " and ac.course.id =:courseId and cs.isActivated = true")
    MyCertificateDTO createMyCertificateDTO(@Param("appUserId") Long appUserId, @Param("courseId") Long courseId);

    boolean existsAssignCourseByAppUser_IdAndCourse_Id(Long appUserId, Long courseId);

    @Query("select count(ac) from AssignCourse ac inner join Course c on ac.course.id = c.id " +
            "inner join c.courseSetting cs " +
            "where ac.deleted = false and ac.progressStatus = :status and ac.appUser.id = :userId and cs.isActivated = true and c.deleted = false")
    int getTotalUserCourseByStatus(Long userId, StatusCourseEnum status);

    @Query("select count(ac) from AssignCourse ac inner join Course c on ac.course.id = c.id " +
            "inner join c.courseSetting cs " +
            "where ac.deleted = false and ac.appUser.id = :userId and cs.isActivated = true and c.deleted = false")
    int getTotalUserCourse(Long userId);

    AssignCourse getDistinctFirstByAppUser_IdAndCourse_Id(Long userId, Long courseId);

    @Query("select count( distinct ls.id) from LessonStructure ls inner join UserLessonProcess ulp on ls.id= ulp.lessonStructure.id " +
            " inner join AssignCourse ac on ulp.assignCourse.id = ac.id" +
            " where ac.deleted = false and ac.course.id = :courseId and ac.appUser.id = :userId ")
    int getTotalUserCoursesByStatus(Long userId, Long courseId);

    @Query("select count( ls.id) from LessonStructure ls where ls.course.id = :courseId ")
    int getTotalUserCourses(Long courseId);

    @Query(value = "select * from assign_course inner join course on assign_course.course_id = course.id " +
            "inner join lesson_structure on lesson_structure.course_id = course.id " +
            "inner join lesson_content_upload on lesson_content_upload.lesson_structure_id = lesson_structure.id " +
            "inner join content_upload on lesson_content_upload.content_upload_id = content_upload.id " +
            "where assign_course.app_user_id = :userId and content_upload.id = :contentUploadId and content_upload.type = 'VIDEO'" +
            "and lesson_structure.id = :lessonStructureId", nativeQuery = true)
    AssignCourse getAssignCourseByAppUser_IdAndContentUploadId(@Param("userId") Long userId, @Param("contentUploadId") Long contentUploadId,
                                                               @Param("lessonStructureId") Long lessonStructureId);

    @Modifying
    @Query("update AssignCourse ac set ac.progressStatus = :progressStatus where ac.toTime < CURRENT_DATE and ac.progressStatus <> 'COMPLETED'")
    void updateOverdueAssignCourse(@Param("progressStatus") StatusCourseEnum progressStatus);

    @Query(value = "select count(ac) from AssignCourse ac where ac.course.id = :courseId " +
            "and ac.progressStatus <> :statusCourse")
    int checkCourseHadUserStudying(@Param("courseId") Long courseId,@Param("statusCourse") StatusCourseEnum statusCourseEnum);
    @Query(value = "select ac.course.id from AssignCourse ac where ac.appUser.id = :userId and ac.deleted = false ")
    List<Long> getCourseIdByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "update AssignCourse ac set ac.lastVisitedCoursePage = CURRENT_TIMESTAMP where ac.appUser.id = :userId " +
            "and ac.course.id = :courseId")
    void updateLastVisitedCoursePage(@Param("userId") Long userId,@Param("courseId") Long courseId);

    @Query(value = "select count(ac) from AssignCourse ac inner join ac.course c inner join c.courseSetting cs " +
            "where ac.deleted = false and ac.appUser.id = :userId and cs.isCertificated = true ")
    int getTotalCertification(Long userId);

    @Query(value = "select count(ac) from AssignCourse ac inner join ac.course c inner join c.courseSetting cs " +
            "where ac.deleted = false and ac.appUser.id = :userId and cs.isCertificated = true " +
            "and ac.progressStatus = :statusCourse " +
            "and (ac.myCertificatePdfLink is not null and ac.myCertificatePdfLink <> '') " +
            " and (ac.myCertificateImageLink is not null and ac.myCertificateImageLink <> '') ")
    int getTotalAwardedCertifications(@Param("userId") Long userId, @Param("statusCourse") StatusCourseEnum statusCourse);

    @Query("select case when ( count(ac) > 0) then true else false end from AssignCourse ac where ac.deleted = false and ac.appUser.id = :id and " +
            "ac.course.id = :courseId")
    boolean checkAssignedCourse(Long courseId, Long id);

    @Query("select new ntlong.dto.AssignCourseDTO(ac.id, ac.course.id, c.name,ac.appUser.fullname, ac.appUser.id, ac.appUser.fullname," +
            " ac.typeAssign, ac.assignDate, ac.fromTime, ac.toTime,ac.progressStatus) " +
            "from AssignCourse ac inner join ac.course c where ac.deleted = false " +
            "and (:courseId is null or c.id = :courseId) " +
            "and (:courseName = '' or :courseName is null or upper(c.name) like concat('%',upper(trim(:courseName)),'%')) " +
            "and (:nameUser = '' or :nameUser is null or upper(ac.appUser.fullname) like concat('%',upper(trim(:nameUser)),'%'))" +
            "and (:typeAssign is null or ac.typeAssign = :typeAssign)")
    Page<AssignCourseDTO> findAllAssignCourses(Pageable paging, String courseName, Long courseId, String nameUser, TypeAssignEnum typeAssign);

    @Query("select new ntlong.dto.AssignCourseDTO(ac.id, ac.course.id, c.name, ac.appUser.fullname, ac.appUser.id, ac.appUser.fullname," +
            " ac.typeAssign, ac.assignDate, ac.fromTime, ac.toTime,ac.progressStatus) " +
            "from AssignCourse ac inner join ac.course c where ac.deleted = false " +
            "and ac.id = :assignCourseId")
    AssignCourseDTO findAssignCourseById(Long assignCourseId);

    @Modifying
    @Transactional
    @Query(value = "update AssignCourse set deleted = true where id in :ids")
    void deleteByIds(List<Long> ids);
}
