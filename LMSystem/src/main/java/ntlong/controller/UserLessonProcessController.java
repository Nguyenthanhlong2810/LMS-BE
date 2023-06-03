package ntlong.controller;

import ntlong.dto.lessonprocess.UserLessonProcessDTO;
import ntlong.service.UserLessonProcessService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-lesson-process")
@Api(tags = "user-lesson-process")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class UserLessonProcessController {
    private final UserLessonProcessService lessonProcessService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createUserLessonProcess(@ModelAttribute UserLessonProcessDTO userLessonProcessDTO){
        try {
            log.debug("==> Create process:");
            return ResponseEntity.ok(lessonProcessService.createLessonProcess(userLessonProcessDTO));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Err on create user lesson process");
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateUserLessonProcess(@ModelAttribute UserLessonProcessDTO userLessonProcessDTO){
        try {
            log.debug("==> Create process:");
            return ResponseEntity.ok(lessonProcessService.updateLessonProcess(userLessonProcessDTO));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Err on create user lesson process");
        }
    }
}
