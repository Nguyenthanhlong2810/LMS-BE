package ntlong.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import ntlong.dto.AdminSettingFirstLoginDTO;
import ntlong.dto.landingPage.LandingPageDTO;
import ntlong.exception.UploadFailException;
import ntlong.response.BaseResponse;
import ntlong.service.AdminService;
import ntlong.service.UserService;
import ntlong.service.landingPage.LandingPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
@Api(tags = "admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminSettingController {

    private final LandingPageService landingPageService;

    private final AdminService adminService;

    @PostMapping(value = "/setFirstLogin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> setFirstLogin(@RequestBody AdminSettingFirstLoginDTO adminSettingDTO) {
        return adminService.adminSetFirstLogin(adminSettingDTO);
    }

    @GetMapping(value = "/setting-first-login")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "Get Admin Setting First Login")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> getAdminSettingFirstLogin() {
        return adminService.getAdminSettingFirstLogin();
    }

    @PostMapping("/landing-page")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The setting doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> postLandingPageSetting(@ModelAttribute LandingPageDTO landingPageDTO) throws IOException, UploadFailException {
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Lưu trang giới thiệu thành công",landingPageService.postLandingPageSetting(landingPageDTO)),HttpStatus.OK);
    }

    @PutMapping("/landing-page")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The setting doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> updateLandingPageSetting(@ModelAttribute LandingPageDTO landingPageDTO) throws IOException, UploadFailException {
        landingPageService.updateLandingPageSetting(landingPageDTO);
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Cập nhật trang giới thiệu thành công",null),HttpStatus.OK);
    }

    @GetMapping("/landing-page")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The setting doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> getLandingPage(){
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Lấy thông tin trang giới thiệu thành công", landingPageService.getLandingPage()), HttpStatus.OK);
    }

    @DeleteMapping("/landing-page")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The setting doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity<BaseResponse> deleteLandingPage(){
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Xóa trang giới thiệu thành công",landingPageService.deleteLandingPage()),HttpStatus.OK);
    }
}
