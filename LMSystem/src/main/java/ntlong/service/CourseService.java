package ntlong.service;

import ntlong.dto.*;
import ntlong.dto.delete.ListIdDTO;
import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.exception.UserNotFoundException;
import ntlong.model.CourseSetting;
import ntlong.payload.request.CourseSettingRequest;
import ntlong.payload.response.course.ProcessUserCourseResponse;
import ntlong.payload.response.course.ProgressUserCourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    CourseDTO saveOrUpdate(CourseDTO courseDTO, MultipartFile[] files, boolean isUpdate) throws IOException;

    Page<CourseResponse> getListCourse(String name, Boolean isActivated, int offset, int limit);

    void deleteById(Long id);

    void deleteByIds(ListIdDTO ids);


    CourseResponse findById(long id);

    Optional<CourseSetting> getSettingCourse(Long id);

    CourseSetting saveOrUpdateSettingCourse(CourseSettingRequest courseSettingRequest);


    CourseStructureDTO getCourseStructureById(Long courseId);

    CourseInformationStructureDTO getLessonStructureById(Long courseId);


    Page<CourseDTO> getSuggestCoursesByUser(Integer pageNo,Integer pageSize, Long userId) throws UserNotFoundException;


    Page<CourseHistoryDTO> getListCourseHistoryByUserId(Long userId, String courseName,
                                                        StatusCourseEnum progressStatus, TypeAssignEnum courseType,
                                                        Date beforeDate, Date afterDate,
                                                        Integer pageNo, Integer pageSize) throws UserNotFoundException;

    CourseOverallDataDTO getCourseOverallDataUpdate(Long courseId,String username);

    Page<CourseDTO> getCourseFilter(Integer pageNo, Integer pageSize, String name, List<String> topics, List<String> experiences, List<String> skills);

    ProcessUserCourseResponse getProcessUserCourses(Long userId);


    ProgressUserCourseResponse getProgressCoursesUser(String username,Long courseId);

    Page<CourseDTO> getHighRatingCourse(Integer pageNo, Integer pageSize);
}
