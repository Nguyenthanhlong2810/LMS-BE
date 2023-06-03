package ntlong.service.faq;

import ntlong.enums.StatusFAQEnum;
import ntlong.payload.request.faq.ManagerTopicRequest;
import ntlong.payload.response.faq.ManagerTopicDTO;
import ntlong.payload.response.faq.ResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManagerTopicService {

    Page<ManagerTopicDTO> topicSearch(String name,StatusFAQEnum status,Pageable pageable);

    List<ManagerTopicDTO> topicQuestionAll(String searchValues);

    ManagerTopicDTO findOne(Long id);

    ResponseDTO updatedTopic(Long id, ManagerTopicRequest managerTopicDTO);

    ResponseDTO createdTopic(ManagerTopicRequest managerTopicDTO);

    ResponseDTO updateDeleteList(List<Long> ids);
}
