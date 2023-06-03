package ntlong.service;

import ntlong.dto.NewsDTO;
import ntlong.exception.NewsNotExistsException;
import ntlong.exception.UploadFailException;
import ntlong.model.LmsNews;
import ntlong.model.NewsContentType;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface LmsNewsService {

    LmsNews createNews(NewsDTO lmsNews, MultipartFile attachment, MultipartFile thumbnail) throws UploadFailException;

    LmsNews updateNews(NewsDTO lmsNews, MultipartFile file, MultipartFile thumbnail) throws NewsNotExistsException, UploadFailException;

    void deleteNews(Long id) throws NewsNotExistsException;

    void deleteNews(Long[] id) throws NewsNotExistsException;

    NewsDTO findById(Long id);

    Page<NewsDTO> getListNews(String keyword,
                              NewsContentType contentType,
                              Boolean status,
                              Boolean isPinned,
                              Integer page,
                              Integer size);

    Page<LmsNews> getListHotNews(Integer page, Integer size, boolean status);

}
