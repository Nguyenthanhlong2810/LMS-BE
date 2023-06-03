package ntlong.controller;

import ntlong.annotation.CurrentUser;
import ntlong.dto.NoteDTO;
import ntlong.model.PaginationResponseModel;
import ntlong.response.BaseResponse;
import ntlong.service.NoteLessonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/note")
@Api(tags = "note")
@RequiredArgsConstructor
public class NoteLessonController {

    private final NoteLessonService noteLessonService;

    @PostMapping
    @ApiOperation("Create note")
    public ResponseEntity<BaseResponse> createNote(@RequestBody NoteDTO noteDTO, @CurrentUser UserDetails userDetails) {
        NoteDTO createdNote = noteLessonService.createNote(noteDTO, userDetails.getUsername());
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Create Note successfully", createdNote), HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation("Update note")
    public ResponseEntity<BaseResponse> updateNote(@RequestBody NoteDTO noteDTO, @CurrentUser UserDetails userDetails) {
        NoteDTO updatedNote = noteLessonService.updateNote(noteDTO, userDetails.getUsername());
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Update Note successfully", updatedNote), HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation("Delete note")
    public ResponseEntity<BaseResponse> deleteNote(@RequestParam(value = "id") Long id) {
        NoteDTO deletedNote = noteLessonService.deleteNote(id);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Delete Note successfully", deletedNote), HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation("Get note")
    public ResponseEntity<BaseResponse> getNotes(@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
                                                 @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
                                                 @RequestParam(value = "lessonContentUploadId", required = false) Long lessonContentUploadId,
                                                 @RequestParam(value = "courseId", required = false) Long courseId,
                                                 @CurrentUser UserDetails userDetails,
                                                 @RequestParam(defaultValue = "createdDate", required = false) String sortBy) {
        Page<NoteDTO> notes = noteLessonService.getNotes(pageNo, pageSize, lessonContentUploadId, courseId, userDetails.getUsername(), sortBy);
        PaginationResponseModel<NoteDTO> res = new PaginationResponseModel<>();
        if (notes.hasContent()) {
            long totalRecords = notes.getTotalElements();
            res = new PaginationResponseModel<>(notes.toList(), totalRecords, pageNo, pageSize);
        }
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Get Note successfully", res), HttpStatus.OK);
    }
}
