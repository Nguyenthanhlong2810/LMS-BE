package ntlong.controller;

import ntlong.dto.MyCourseDetailDTO;
import ntlong.dto.MyLearningHistoryDTO;
import ntlong.response.BaseResponse;
import ntlong.service.MyCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/my-course")
@RestController
@Api
@RequiredArgsConstructor
public class MyCourseController {

    private final MyCourseService myCourseService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "My Course Detail", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<BaseResponse> findMyCourseDetail(@RequestParam(value = "courseId") Long courseId,
                                                           @RequestParam(value = "isPreview", required = false)
                                                           Boolean isPreview) {
        String username = getUserNameOfUserLogin();
        MyCourseDetailDTO myCourseDetailDTO = myCourseService.findMyCourseDetail(username, courseId, isPreview);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.toString(), myCourseDetailDTO), HttpStatus.OK);
    }

    @PostMapping("/add-history")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "Add My Learning History", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<BaseResponse> modifyMyLearningHistory(@RequestBody MyLearningHistoryDTO myLearningHistoryDTO) {
        String username = getUserNameOfUserLogin();
        myCourseService.modifyMyLearningHistory(myLearningHistoryDTO, username);
        BaseResponse baseResponse = new BaseResponse("");
        baseResponse.setMessage("Modify Learning History Success");
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/server/download")
    public void downloadFile(@RequestParam(value = "fileURL") String fileURL , HttpServletResponse response) throws IOException {
        String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, headerValue);
        InputStream inputStream = myCourseService.downloadFile(fileURL);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        StreamUtils.copy(inputStreamResource.getInputStream(), response.getOutputStream());
    }

    private String getUserNameOfUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
