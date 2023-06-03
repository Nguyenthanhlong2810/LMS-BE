package ntlong.controller;

import ntlong.annotation.CurrentUser;
import ntlong.dto.MyCertificateDTO;
import ntlong.model.CourseSetting;
import ntlong.model.PaginationResponseModel;
import ntlong.payload.response.CertificateResponse;
import ntlong.repository.CourseSettingRepository;
import ntlong.response.BaseResponse;
import ntlong.service.MyCertificateService;
import ntlong.service.impl.MyCertificateServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/myCertificate")
@Api("My Certificate")
@Slf4j
@AllArgsConstructor
public class MyCertificateController {

    private final MyCertificateService myCertificateService;

    private final CourseSettingRepository courseSettingRepository;

    @GetMapping("/create/{courseId}")
    @ApiOperation("Search My Certificate")
    public ResponseEntity<BaseResponse> createCertificate(@PathVariable("courseId") Long courseId,
                                                          @CurrentUser UserDetails userDetails) {
        log.debug("Create Certificate");
        String username = userDetails.getUsername();
        CertificateResponse certificateResponse = myCertificateService.createCertificate(courseId,username);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.toString(),certificateResponse), HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation("Search My Certificate")
    public ResponseEntity<BaseResponse> getMyCertificatesFilter(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(value = "keyword", required = false) String keyword) {
        log.debug("Search My Certificate");
        String username = getUserNameOfUserLogin();
        if (isKeywordContainSpecialCharacter(keyword))
            return new ResponseEntity<>(new BaseResponse("Keyword must be not contain special character!"), HttpStatus.BAD_REQUEST);
        Page<MyCertificateDTO> myCertificateDTOPage = myCertificateService.getMyCertificatesFilter(username, pageNo, pageSize, keyword);
        return new ResponseEntity<>(
                new BaseResponse(HttpStatus.OK.toString()
                        , new PaginationResponseModel<>(myCertificateDTOPage.getContent()
                        , myCertificateDTOPage.getTotalElements(), pageNo, pageSize))
                , HttpStatus.OK);
    }

    @GetMapping("/detail")
    @ApiOperation("My Certificate Detail")
    public ResponseEntity<BaseResponse> findMyCertificateDetail(@RequestParam(value = "courseId", required = false) Long courseId) throws Exception {
        log.debug("My Certificate Detail");
        String username = getUserNameOfUserLogin();
        CourseSetting courseSetting = courseSettingRepository.findByCourseId(courseId);
        if(!courseSetting.getIsCertificated()){
            return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                    "Khóa học không cấp chứng chỉ" ), HttpStatus.OK);
        }
        MyCertificateDTO myCertificateDTO = myCertificateService.findMyCertificateDetail(username, courseId);
        if(myCertificateDTO == null){
            return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                    "Bạn sẽ nhận được chứng nhận hoặc chứng chỉ hoàn thành khóa học sau khi hoàn thành toàn bộ nội dung khóa học" ), HttpStatus.OK);
        }
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.toString(), myCertificateDTO), HttpStatus.OK);
    }

    @GetMapping("/download/detail")
    @ApiOperation("Download My Certificate Detail")
    public void downloadMyCertificateDetailByType(@RequestParam(value = "courseId") Long courseId
            , @RequestParam(value = "type") String type
            , HttpServletResponse response ) throws IOException {
        log.debug("My Certificate Detail");
        String username = getUserNameOfUserLogin();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerValue = "attachment; filename=certificate_" + currentDateTime + "." + type;
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, headerValue);
        myCertificateService.downloadMyCertificateDetailByType(username, courseId, type.toLowerCase(), response);

    }

    @GetMapping("/download-multi")
    @ApiOperation("My Certificate Detail")
    public void downloadMyCertificatesByType(@RequestParam(value = "courseIds") String courseIds
            , @RequestParam(value = "type") String type, HttpServletResponse response) throws Exception {
        String username = getUserNameOfUserLogin();
        List<String> courseId = List.of(courseIds.split(","));
        List<Long> ids = courseId.stream().map(Long::parseLong).collect(Collectors.toList());
        if (ids.size() == 1) {
            response.setHeader("Content-Disposition", "attachment; filename=download-certificates." + type);
            myCertificateService.downloadMyCertificateDetailByType(username, ids.get(0), type, response);
        } else {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=download-certificates.zip");
            myCertificateService.downloadCertificatesAsZip(username, ids, type, response);
        }
    }

    @GetMapping("/preview")
    @ApiOperation("Preview Certificate")
    public ResponseEntity<BaseResponse> getPreviewCertificate(@RequestParam(value = "courseId") Long courseId){
        String username = getUserNameOfUserLogin();
        CertificateResponse certificateResponse = myCertificateService.getPreviewCertificate(courseId, username);
        ResponseEntity<BaseResponse> response = new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Get preview certificate successfully",certificateResponse),HttpStatus.OK);
        return response;
    }

    /**
     * Check từ khóa có chứa kí tự đặc biệt hay không
     * <p>Return true nếu từ khóa tìm kiếm có chứa kí tự đặc biệt</p>
     *
     * @param keyword
     * @return boolean
     */
    private boolean isKeywordContainSpecialCharacter(String keyword) {
        if (keyword == null) // Trường hợp courseName không có trên request URL -> bypass -> get all my certificate
            return false;
        String specialCharactersString ="[~!@#$%^&*()_+{}\\[\\]:;,.<>/?-]";
        Pattern pattern = Pattern.compile(specialCharactersString);
        Matcher matcher = pattern.matcher(keyword);
        return matcher.find();
    }

    private String getUserNameOfUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
