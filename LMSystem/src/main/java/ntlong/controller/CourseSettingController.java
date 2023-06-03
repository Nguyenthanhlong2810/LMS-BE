package ntlong.controller;

import ntlong.model.CourseSetting;
import ntlong.payload.request.CourseSettingRequest;
import ntlong.payload.response.CourseSettingResponseDTO;
import ntlong.response.BaseResponse;
import ntlong.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/course-setting")
public class CourseSettingController {

    private final CourseService courseService;

    public CourseSettingController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getSettingCourse(@PathVariable("courseId") long courseId) {
        Optional<CourseSetting> settingCourse = courseService.getSettingCourse(courseId);
        return new ResponseEntity<>(new BaseResponse("get setting success", settingCourse), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> saveCourseSetting(@RequestBody CourseSettingRequest courseSettingRequest) {
        CourseSettingResponseDTO.fromEntity(courseService.saveOrUpdateSettingCourse(courseSettingRequest));
        return new ResponseEntity<>(new BaseResponse("save setting success"), HttpStatus.OK);
    }

}
