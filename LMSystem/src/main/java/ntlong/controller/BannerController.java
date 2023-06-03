package ntlong.controller;

import ntlong.dto.banner.BannerDTO;
import ntlong.dto.banner.BannerType;
import ntlong.dto.banner.BannerUpdateDTO;
import ntlong.exception.UploadFailException;
import ntlong.service.BannerService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/banner")
@AllArgsConstructor
public class BannerController {


    private final BannerService bannerService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createBanner(@ModelAttribute BannerDTO bannerDTO) throws UploadFailException {
        try {
            log.debug("==> Create Banner:");
            return ResponseEntity.ok(bannerService.createBanner(bannerDTO));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi tạo banner");
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateBanner(@ModelAttribute BannerUpdateDTO bannerDTO) throws UploadFailException {
        try {
            log.debug("==> update Banner");
            return ResponseEntity.ok(bannerService.updateBanner(bannerDTO));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi cập nhật banner");
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteBanner(){
        try {
            log.debug("==> Create Banner");
            bannerService.deleteBanner();
            return ResponseEntity.ok("Xóa banner thành công ");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi xóa banner");
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity getBanner(@RequestParam(value = "type", required = false) BannerType bannerType) {
        return ResponseEntity.ok(bannerService.getBanner(bannerType));
    }

}
