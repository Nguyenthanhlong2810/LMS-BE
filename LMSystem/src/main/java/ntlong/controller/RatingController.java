package ntlong.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import ntlong.annotation.CurrentUser;
import ntlong.dto.NoteDTO;
import ntlong.dto.RatingDTO;
import ntlong.model.PaginationResponseModel;
import ntlong.payload.request.RatingCourseRequest;
import ntlong.payload.response.RatingResponse;
import ntlong.response.BaseResponse;
import ntlong.service.RatingCourseService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rating")
public class RatingController {

    private final RatingCourseService ratingCourseService;

    @PostMapping
    public ResponseEntity<BaseResponse> createRating(@RequestBody RatingCourseRequest ratingCourseRequest,
                                                      HttpServletRequest req,
                                                      @CurrentUser UserDetails userDetails) throws IOException {
        String username = userDetails.getUsername();
        ratingCourseService.createRatingCourse(ratingCourseRequest,username);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(), "Gửi đánh giá thành công",
                null), HttpStatus.OK);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<BaseResponse> getRatingCourse(@PathVariable Long courseId,
                                                     HttpServletRequest req) throws IOException {

        RatingResponse rate = ratingCourseService.getRatingCourse(courseId);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(), "Get rating successfully",
                rate), HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation("Get rating")
    public ResponseEntity<BaseResponse> getNotes(@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
                                                 @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
                                                 @RequestParam(value = "courseId", required = false) Long courseId,
                                                 @RequestParam(value = "star", required = false) int rate,
                                                 @RequestParam(defaultValue = "createdDate", required = false) String sortBy) {
        Page<RatingDTO> rates = ratingCourseService.getRatings(pageNo, pageSize, courseId, rate, sortBy);
        PaginationResponseModel<RatingDTO> res   = new PaginationResponseModel<>();
        if (rates.hasContent()) {
            long totalRecords = rates.getTotalElements();
            res = new PaginationResponseModel<>(rates.toList(), totalRecords, pageNo, pageSize);
        }
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Get Note successfully", res), HttpStatus.OK);
    }
}
