package ntlong.service;

import ntlong.dto.AssignCourseDTO;
import ntlong.dto.CourseHistoryDTO;
import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.model.FileUpload;
import ntlong.payload.request.AssignCourseRequest;
import ntlong.payload.response.CourseAssignedResponse;
import ntlong.payload.response.UserAssignedResponse;
import ntlong.response.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface AssignCourseService {


    AssignCourseDTO updateCompletedCourse(Long id, Long courseId) throws Exception;

    void updateLastVisitedCoursePage(Long userId, Long courseId);

    AssignCourseDTO createAssignCourse(String username, Long courseId, TypeAssignEnum typeAssignEnum, StatusCourseEnum status);

    AssignCourseDTO getAssignCourse(Long userId, Long courseId);

    Page<AssignCourseDTO> getAllAssignCourse(Integer pageNo, Integer pageSize,
                                             String courseName, Long courseId,String nameUser,
                                             TypeAssignEnum typeAssign);

    AssignCourseDTO getAssignCourseById(Long assignCourseId);

    void cancelAssignCourse(Long courseId, String username);

    void resetProgressCourse(Long courseId, String username);

    void deleteByIds(List<Long> ids);
}
