package ntlong.controller;

import lombok.extern.log4j.Log4j2;
import ntlong.annotation.CurrentUser;
import ntlong.dto.AssignCourseDTO;
import ntlong.dto.CourseHistoryDTO;
import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.exception.BusinessException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.AppUser;
import ntlong.model.FileUpload;
import ntlong.model.PaginationResponseModel;
import ntlong.payload.request.AssignCourseRequest;
import ntlong.payload.response.CourseAssignedResponse;
import ntlong.payload.response.UserAssignedResponse;
import ntlong.repository.UserRepository;
import ntlong.response.BaseResponse;
import ntlong.response.ResponseMessage;
import ntlong.service.AssignCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/assign-course")
@Api(tags = "assign-course")
@RequiredArgsConstructor
public class AssignCourseController {

    private final AssignCourseService assignCourseService;
    private final UserRepository userRepository;

    @PostMapping("/set-last-visited-course")
    public ResponseEntity<BaseResponse> updateLastVisitedCoursePage(@RequestParam(value = "courseId") Long courseId,
                                                              @CurrentUser UserDetails userDetails) {
        AppUser user = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(userDetails.getUsername());
        assignCourseService.updateLastVisitedCoursePage(user.getId(), courseId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
                "Update last visited course successfully!"));
    }

    @PostMapping("/create-assign-course")
    public ResponseEntity<BaseResponse> createAssignCourse(@RequestParam(value = "courseId") Long courseId,
                                                                    @CurrentUser UserDetails userDetails) {
        AppUser user = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(userDetails.getUsername());
        AssignCourseDTO createdAssignCourse = assignCourseService
                .createAssignCourse(user.getUsername(), courseId, TypeAssignEnum.FREE, StatusCourseEnum.UNCOMPLETED);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
                "Đăng ký khóa học thành công",createdAssignCourse));
    }

    @GetMapping("/get-assign-course")
    public ResponseEntity<BaseResponse> getAssignedCourse(@RequestParam(value = "userId") Long userId,
                                                           @RequestParam(value = "courseId") Long courseId) {
        AssignCourseDTO createdAssignCourse = assignCourseService.getAssignCourse(userId,courseId);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
                "Get assign course successfully!",createdAssignCourse));
    }

    @GetMapping("/course/assigned")
    public ResponseEntity<BaseResponse> getAllAssignCourse(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(value = "pageSize", defaultValue = "25") Integer pageSize,
                                                           @RequestParam(value = "nameCourse", required = false) String courseName,
                                                           @RequestParam(value = "courseId", required = false) Long courseId,
                                                           @RequestParam(value = "nameUser", required = false) String nameUser,
                                                           @RequestParam(value = "typeAssign", required = false) TypeAssignEnum typeAssign) {
        Page<AssignCourseDTO> assignCourseDTOList = assignCourseService
                .getAllAssignCourse(pageNo, pageSize,courseName,courseId,nameUser,typeAssign);
        PaginationResponseModel<AssignCourseDTO> res = new PaginationResponseModel<>();
        if (assignCourseDTOList.hasContent()) {
            long totalRecords = assignCourseDTOList.getTotalElements();
            res = new PaginationResponseModel<>(assignCourseDTOList.toList(), totalRecords, pageNo, pageSize);
        }
        return new ResponseEntity<>(new BaseResponse("Lấy danh sách khóa học đã gán thành công", res), HttpStatus.OK);
    }

    @GetMapping("/{assignCourseId}")
    public ResponseEntity<BaseResponse> getAssignedCourse(@PathVariable Long assignCourseId) {
        AssignCourseDTO assignCourseDTO = assignCourseService.getAssignCourseById(assignCourseId);
        return new ResponseEntity<>(new BaseResponse("Lấy thông tin khóa học đã gán thành công", assignCourseDTO), HttpStatus.OK);
    }

    @PutMapping("/cancel/{courseId}")
    public ResponseEntity<BaseResponse> cancelAssignCourse(@PathVariable Long courseId,
                                                           @CurrentUser UserDetails user) {
        String username = user.getUsername();
        assignCourseService.cancelAssignCourse(courseId,username);
        return new ResponseEntity<>(new BaseResponse("Hủy gán khóa học thành công", null), HttpStatus.OK);
    }

    @PutMapping("/delete-multi")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> deleteAssignCourse(@RequestBody List<Long> ids) throws BusinessException {
        assignCourseService.deleteByIds(ids);
        return ResponseEntity.ok(new ResponseMessage("Hủy gán khóa học thành công"));
    }

    @PutMapping("/reset/{courseId}")
    public ResponseEntity<BaseResponse> resetAssignCourse(@PathVariable Long courseId,
                                                           @CurrentUser UserDetails user) {
        String username = user.getUsername();
        assignCourseService.resetProgressCourse(courseId,username);
        return new ResponseEntity<>(new BaseResponse("Đặt lại tiến độ học tập thành công", null), HttpStatus.OK);
    }
}