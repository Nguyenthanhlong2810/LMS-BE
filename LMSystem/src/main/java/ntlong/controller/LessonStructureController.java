package ntlong.controller;

import ntlong.dto.LessonStructureDTO;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.PaginationResponseModel;
import ntlong.payload.request.SortCourseStructureRequest;
import ntlong.response.BaseResponse;
import ntlong.response.ResponseMessage;
import ntlong.service.LessonStructureService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/lesson-structure")
@Api(tags = "lesson structure")
@RequiredArgsConstructor
public class LessonStructureController {

    private final LessonStructureService lessonStructureService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<BaseResponse> searchLessonStructures(@RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
        log.debug("==> Get LessonStructure param {} , {}", pageNo, pageSize);

        Page<LessonStructureDTO> dtoPage = lessonStructureService.searchLessonStructure(PageRequest.of(pageNo - 1, pageSize, Sort.by("sortOrder")));
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.toString(), new PaginationResponseModel<>(dtoPage.getContent(), dtoPage.getTotalElements(), pageNo, pageSize)), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<BaseResponse> searchLessonStructureById(@PathVariable("id") Long id) throws ResourceNotFoundException {
        log.debug("==> Get LessonStructure By Id {}", id);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.toString(), lessonStructureService.findById(id)), HttpStatus.OK);

    }

    @GetMapping("/course-id/{courseId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<BaseResponse> searchLessonStructureByCourseId(@PathVariable("courseId") Long courseId) throws ResourceNotFoundException {
        log.debug("==> Get LessonStructure By Course Id {}", courseId);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.toString(), lessonStructureService.findByCourseId(courseId)), HttpStatus.OK);

    }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> createLessonStructure(@Valid @RequestBody LessonStructureDTO lessonStructureDTO) {
            log.debug("==> Create LessonStructure: {}", lessonStructureDTO);
            LessonStructureDTO lessonStructure = lessonStructureService.createLessonStructure(lessonStructureDTO);
            return new ResponseEntity<>(new ResponseMessage(HttpStatus.OK.value(), "Create LessonStructure Success", lessonStructure, 1L), HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> updateLessonStructure(@RequestBody @Valid LessonStructureDTO request) throws ResourceNotFoundException {
            log.debug("==> Update LessonStructure: {}", request);
            LessonStructureDTO lessonStructureDTO = lessonStructureService.updateLessonStructure(request);
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(),"Update Content Upload Success",lessonStructureDTO, 1L));
    }

    @PutMapping("/delete-multi")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> deleteIdsLessonStructure(@RequestBody List<Long> ids) throws ResourceNotFoundException {
            log.debug("==> Delete by list id Content Upload: {}", ids);
            lessonStructureService.deleteByIds(ids);
            return new ResponseEntity<>(new ResponseMessage("Delete LessonStructure By Ids Success"), HttpStatus.OK);
    }

    @PutMapping("/sort-structure")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> swapSortOrderLessonStructure(@RequestBody SortCourseStructureRequest request) throws ResourceNotFoundException {
            log.debug("==> Update SortOrder LessonStructure: {}", request);
            lessonStructureService.swapSortOrderLessonStructure(request);
            return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(), "Update SortOrder Success"), HttpStatus.OK);
    }
}