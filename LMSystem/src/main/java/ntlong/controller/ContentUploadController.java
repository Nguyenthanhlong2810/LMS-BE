package ntlong.controller;

import ntlong.dto.ContentUploadDTO;
import ntlong.enums.TypeContentUploadEnum;
import ntlong.exception.BusinessException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.PaginationResponseModel;
import ntlong.response.BaseResponse;
import ntlong.response.ResponseMessage;
import ntlong.service.ContentUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/content-upload")
@Api(tags = "contentUpload")
@RequiredArgsConstructor
public class ContentUploadController {

    private final ContentUploadService contentUploadService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<BaseResponse> searchContentUploads(@RequestParam(defaultValue = "1") Integer pageNo,
                                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                                             @RequestParam(value = "keySearch",required = false) String keySearch,
                                                             @RequestParam(value = "type",required = false) TypeContentUploadEnum type,
                                                             @RequestParam(defaultValue = "lastUpdated") String sortBy) {
        log.debug("==> Get Content Upload");
        Page<ContentUploadDTO> dtoPage=contentUploadService.searchContenUploads(pageNo, pageSize, keySearch, type, sortBy);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(),new PaginationResponseModel<>(dtoPage.getContent(),dtoPage.getTotalElements(), pageNo, pageSize)));

    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<BaseResponse> searchContentById(@PathVariable("id") Long id) throws ResourceNotFoundException {
        log.debug("==> Get Content Upload By Id {}",id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(),contentUploadService.findById(id)));

    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> createContentUpload(@RequestPart MultipartFile[] file,
                                                               @RequestPart String request) throws IOException {
            log.debug("==> Create Content Upload: {}", request);
            List<ContentUploadDTO> contentUploadDTOs=contentUploadService.createContent(new ObjectMapper().readValue(request, ContentUploadDTO.class), file);
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.CREATED.value(),"Create Content Upload Success", contentUploadDTOs,Long.parseLong(String.valueOf(contentUploadDTOs.size()))));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> updateContentUpload(@RequestBody @Valid ContentUploadDTO request) throws ResourceNotFoundException {
            log.debug("==> Update Content Upload : {}", request);
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.toString(), contentUploadService.updateContent(request)));
    }

    @PutMapping("/delete-multi")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> deleteContentUploadByIds(@RequestBody List<Long> ids) throws BusinessException {
            log.debug("==> Delete by list id Content Upload: {}", ids);
            contentUploadService.deleteByIds(ids);
            return ResponseEntity.ok(new ResponseMessage("Delete Content Upload Success"));
    }
}