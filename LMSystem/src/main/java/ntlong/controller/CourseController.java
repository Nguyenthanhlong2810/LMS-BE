package ntlong.controller;

import ntlong.annotation.CurrentUser;
import ntlong.dto.*;
import ntlong.dto.delete.ListIdDTO;
import ntlong.enums.StatusCourseEnum;
import ntlong.enums.TypeAssignEnum;
import ntlong.exception.UserNotFoundException;
import ntlong.model.PaginationResponseModel;
import ntlong.payload.response.course.ProcessUserCourseResponse;
import ntlong.payload.response.course.ProgressUserCourseResponse;
import ntlong.response.BaseResponse;
import ntlong.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/course")
@Api(tags = "course")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${CourseController.create}", authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
//            @ApiResponse(code = 404, message = "The creating doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> create(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                    @RequestPart("courseJson") String courseJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CourseDTO courseDTO = mapper.readValue(courseJson, CourseDTO.class);
        CourseDTO savedCourse = courseService.saveOrUpdate(courseDTO, files, false);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(), "Create course successfully", savedCourse), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(new BaseResponse("get list success", courseService.findById(id)), HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${CourseController.update}", authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> update(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                    @RequestPart("courseJson") String courseJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CourseDTO courseDTO;
        courseDTO = mapper.readValue(courseJson, CourseDTO.class);
        courseService.saveOrUpdate(courseDTO, files, true);
        return new ResponseEntity<>("Cập nhật khóa học thành công!", HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${CourseController.delete}", authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity delete(@RequestParam Long id) {
        courseService.deleteById(id);
        return new ResponseEntity<>("Xóa khóa học thành công", HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteMulti")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${CourseController.delete}", authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity deleteMulti(@RequestBody ListIdDTO listId) {
        courseService.deleteByIds(listId);
        return new ResponseEntity<>("Xóa thành công",HttpStatus.OK);
    }

    @GetMapping("/get-courses")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "${CourseController.list}", authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getListCourse(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "isActivated", required = false) Boolean isActivated,
                                           @RequestParam(value = "offset", defaultValue = "1") int offset,
                                           @RequestParam(value = "limit", defaultValue = "25") int limit) {
        Page<CourseResponse> dtoPage = courseService.getListCourse(name, isActivated, offset, limit);

        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.toString(),
                new PaginationResponseModel<>(dtoPage.getContent(), dtoPage.getTotalElements(), offset, limit)), HttpStatus.OK);
    }

    @GetMapping("course-structure/{courseId}")
    public ResponseEntity<BaseResponse> getListCourseLessonStructure(@PathVariable("courseId") Long courseId) {
        return new ResponseEntity<>(new BaseResponse("get list success",
                courseService.getCourseStructureById(courseId)), HttpStatus.OK);
    }

    @GetMapping("course-information-structure/{courseId}")
    @ApiOperation(value = "Get Lesson Of Course", notes = "Course Controller")
    @ApiResponses(value = {//
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "Course is not found"), //
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> getListLessonStructure(@PathVariable("courseId") Long courseId) {
        return new ResponseEntity<>(new BaseResponse("get list lesson success",courseService.getLessonStructureById(courseId)), HttpStatus.OK);
    }


    @GetMapping("course-overall/{courseId}")
    public ResponseEntity<BaseResponse> getCourseOverallData(@PathVariable(value = "courseId") Long courseId,
                                                             @CurrentUser UserDetails userDetails) {
        String username = userDetails.getUsername();
        return new ResponseEntity<>(new BaseResponse("get course overall success", courseService.getCourseOverallDataUpdate(courseId,username)), HttpStatus.OK);
    }

    @GetMapping("suggest-courses")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getSuggestCourses(@RequestParam(value = "userId") Long userId,
                                            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) throws UserNotFoundException {
        log.debug("get list course");
        Page<CourseDTO> courses = courseService.getSuggestCoursesByUser(pageNo, pageSize, userId);
        PaginationResponseModel<CourseDTO> res = new PaginationResponseModel<>();
        if (courses.hasContent()) {
            long totalRecords = courses.getTotalElements();
            res = new PaginationResponseModel<>(courses.toList(), totalRecords, pageNo, pageSize);
        }
        return new ResponseEntity<>(new BaseResponse("get list recommend course successfully", res), HttpStatus.OK);
    }

    @GetMapping("/high-rating-courses")
    public ResponseEntity getHighRatingCourses(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) throws UserNotFoundException {
        log.debug("get list course");
        Page<CourseDTO> courses = courseService.getHighRatingCourse(pageNo, pageSize);
        PaginationResponseModel<CourseDTO> res = new PaginationResponseModel<>();
        if (courses.hasContent()) {
            long totalRecords = courses.getTotalElements();
            res = new PaginationResponseModel<>(courses.toList(), totalRecords, pageNo, pageSize);
        }
        return new ResponseEntity<>(new BaseResponse("get list recommend course successfully", res), HttpStatus.OK);
    }

    @GetMapping("/course-history")
    public ResponseEntity getListHistoryFilter(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(value = "userId") Long userId,
                                               @RequestParam(value = "courseName", required = false) String courseName,
                                               @RequestParam(value = "beforeDate", required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date beforeDate,
                                               @RequestParam(value = "afterDate", required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date afterDate,
                                               @RequestParam(value = "courseType", required = false) TypeAssignEnum courseType,
                                               @RequestParam(value = "progressStatus", required = false) StatusCourseEnum progressStatus) throws UserNotFoundException {
//        Date before;
//        if(Objects.isNull(beforeDate)){
//            before = new SimpleDateFormat("yyyy-MM-dd").parse(beforeDate);
//        }
        Page<CourseHistoryDTO> courseHistory = courseService.getListCourseHistoryByUserId(userId, courseName,
                progressStatus, courseType, beforeDate, afterDate, pageNo, pageSize);
        PaginationResponseModel<CourseHistoryDTO> res = new PaginationResponseModel<>();
        if (courseHistory.hasContent()) {
            long totalRecords = courseHistory.getTotalElements();
            res = new PaginationResponseModel<>(courseHistory.toList(), totalRecords, pageNo, pageSize);
        }
        return new ResponseEntity<>(new BaseResponse("get list success", res), HttpStatus.OK);
    }

    @GetMapping("/course-filter")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getCourseFilter(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "topics", required = false) List<String> topics,
                                          @RequestParam(value = "experiences", required = false) List<String> experiences,
                                          @RequestParam(value = "skills", required = false) List<String> skills) {
        log.debug("get list course");
        Page<CourseDTO> coursePage = courseService.getCourseFilter(pageNo, pageSize, name, topics, experiences, skills);
        PaginationResponseModel<CourseDTO> res = new PaginationResponseModel<>();
        if (coursePage.hasContent()) {
            long totalRecords = coursePage.getTotalElements();
            res = new PaginationResponseModel<>(coursePage.toList(), totalRecords, pageNo, pageSize);
        }
        return new ResponseEntity<>(new BaseResponse("get list success", res), HttpStatus.OK);
    }

    @GetMapping("/learning-progress")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getProcessUserCourses(@RequestParam(value = "userId") Long userId) {
        log.debug("get process user course");
        ProcessUserCourseResponse processUserCourseResponse = courseService.getProcessUserCourses(userId);
        return new ResponseEntity<>(new BaseResponse("get process user course successfully", processUserCourseResponse), HttpStatus.OK);
    }

    @GetMapping("/course-progress")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getProgressCoursesUser(@RequestParam(value = "courseId") Long courseId) {
        try {
            log.debug("get list course by user");
            ProgressUserCourseResponse progressUserCourseResponse = courseService.getProgressCoursesUser(getUserNameOfUserLogin(), courseId);
            return new ResponseEntity<>(new BaseResponse("get list courses by user successfully", progressUserCourseResponse), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new BaseResponse(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private String getUserNameOfUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
