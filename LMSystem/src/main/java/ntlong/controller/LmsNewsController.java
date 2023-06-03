package ntlong.controller;

import lombok.AllArgsConstructor;
import ntlong.dto.NewsDTO;
import ntlong.exception.NewsNotExistsException;
import ntlong.exception.UploadFailException;
import ntlong.model.LmsNews;
import ntlong.model.NewsContentType;
import ntlong.model.PaginationResponseModel;
import ntlong.response.BaseResponse;
import ntlong.service.LmsNewsService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/news")
@AllArgsConstructor
public class LmsNewsController {
    private final LmsNewsService lmsNewsService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Gson gson = new Gson();

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createNews(@RequestPart MultipartFile file,
                                     @RequestPart(required = false) MultipartFile thumbnail,
                                     @RequestPart String request) {
        NewsDTO newsDTO = gson.fromJson(request, NewsDTO.class);
        log.debug("create news: {}", newsDTO.getSubject());
        return ResponseEntity.ok(lmsNewsService.createNews(newsDTO, file, thumbnail));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateNews(@RequestPart(required = false) MultipartFile attachment,
                                     @RequestPart(required = false) MultipartFile thumbnail,
                                     @RequestPart String request) throws NewsNotExistsException, UploadFailException {
        NewsDTO lmsNews = gson.fromJson(request, NewsDTO.class);
        log.debug("update news: {}", lmsNews.getId());
        return ResponseEntity.ok(lmsNewsService.updateNews(lmsNews, attachment, thumbnail));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity removeNews(@RequestParam Long id) throws NewsNotExistsException {
        log.debug("delete news: {}", id);
        lmsNewsService.deleteNews(id);
        return ResponseEntity.ok("deleted news: " + id);
    }

    @DeleteMapping("/bulkDelete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity removeListNews(@RequestBody Long[] ids) throws NewsNotExistsException {
        log.debug("delete news: {}", ids);

        lmsNewsService.deleteNews(ids);
        return ResponseEntity.ok("deleted news: " + ids);
    }

    @GetMapping("/{id}")
    public ResponseEntity getNewsById(@PathVariable("id") Long id) {
        log.debug("get news: {}", id);
        return ResponseEntity.ok(lmsNewsService.findById(id));
    }

    @GetMapping("/list")
    public ResponseEntity getListNews(@RequestParam(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "contentType", required = false) NewsContentType contentType,
                                      @RequestParam(value = "status", required = false) Boolean status,
                                      @RequestParam(value = "isPinned", required = false) Boolean isPinned,
                                      @RequestParam(defaultValue = "1", value = "pageNo") Integer page,
                                      @RequestParam(defaultValue = "25", value = "pageSize") Integer size) {
        log.debug("get list news");
        Page<NewsDTO> lmsNewsPage = lmsNewsService.getListNews(keyword, contentType, status, isPinned, page, size);
        PaginationResponseModel<NewsDTO> res = new PaginationResponseModel<>();
        if (lmsNewsPage.hasContent()) {
            long totalRecords = lmsNewsPage.getTotalElements();
            res = new PaginationResponseModel<>(lmsNewsPage.toList(), totalRecords, page, size);
        }
        return new ResponseEntity<>(new BaseResponse("get list success", res), HttpStatus.OK);
    }

    @GetMapping("/hot-news")
    public ResponseEntity getListHotNews(@RequestParam(defaultValue = "0", value = "pageNo") Integer page,
                                         @RequestParam(defaultValue = "10", value = "pageSize") Integer size,
                                         @RequestParam(value = "status") Boolean status) {
        log.debug("get list hot news");
        Page<LmsNews> lmsNewsPage = lmsNewsService.getListHotNews(page, size, status);
        PaginationResponseModel<LmsNews> res = new PaginationResponseModel<>();
        if (lmsNewsPage.hasContent()) {
            long totalRecords = lmsNewsPage.getTotalElements();
            res = new PaginationResponseModel<>(lmsNewsPage.toList(), totalRecords, page, size);
        }
        return new ResponseEntity<>(new BaseResponse("get list success", res), HttpStatus.OK);
    }

}
