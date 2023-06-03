package ntlong.service.faq;

import ntlong.enums.StatusFAQEnum;
import ntlong.payload.request.faq.ManagerQuestionRequest;
import ntlong.payload.response.faq.ManagerQuestionDTO;
import ntlong.payload.response.faq.ResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by txtrung
 * Date: 08/06/2022
 * Time: 14:10
 * Project name: lms-faq
 */

public interface ManagerQuestionService {

    Page<ManagerQuestionDTO> questionSearch(String searchValues, StatusFAQEnum status,Long topicId, Pageable pageable);

    ManagerQuestionDTO findOne(Long id);

    ResponseDTO createQuestion(MultipartFile[] file, ManagerQuestionRequest questionRequest) throws IOException;

    ResponseDTO updateQuestion(MultipartFile[] file, ManagerQuestionRequest questionRequest, Long id) throws IOException;

    ResponseDTO updateDeleteList(List<Long> ids);
}
