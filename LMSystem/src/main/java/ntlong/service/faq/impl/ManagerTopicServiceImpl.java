package ntlong.service.faq.impl;

import lombok.AllArgsConstructor;
import ntlong.enums.StatusFAQEnum;
import ntlong.exception.faq.CustomException;
import ntlong.exception.faq.EntityNotFoundException;
import ntlong.model.faq.ManagerQuestion;
import ntlong.model.faq.ManagerTopic;
import ntlong.payload.request.faq.ManagerTopicRequest;
import ntlong.payload.response.faq.ManagerQuestionDTO;
import ntlong.payload.response.faq.ManagerTopicDTO;
import ntlong.payload.response.faq.ResponseDTO;
import ntlong.repository.faq.ManagerTopicRepository;
import ntlong.service.faq.ManagerTopicService;
import ntlong.utils.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ManagerTopicServiceImpl implements ManagerTopicService {

    private final ManagerTopicRepository managerTopicRepository;
    private final Common common;

    @Override
    public Page<ManagerTopicDTO> topicSearch(String name,StatusFAQEnum status, Pageable pageable) {
        Page<ManagerTopic> managerTopicPage = managerTopicRepository.findAllBySearch(name,status,pageable);
        List<ManagerTopicDTO> topicDTOS = new ArrayList<>();
        managerTopicPage.getContent().forEach(x -> {
            ManagerTopicDTO managerTopicDTO = new ManagerTopicDTO(x.getId(), x.getCode(), x.getName(), x.getStatusTopic());
            topicDTOS.add(managerTopicDTO);
        });
        return new PageImpl<>(topicDTOS, pageable, managerTopicPage.getTotalElements());
    }

    @Override
    @Transactional
    public List<ManagerTopicDTO> topicQuestionAll(String searchValues) {
        List<ManagerTopic> managerTopics = managerTopicRepository.findAllBySearchParams(
                searchValues);

        List<ManagerTopicDTO> topicDTOS = new ArrayList<>();

        managerTopics.forEach(x -> {
            ManagerTopicDTO managerTopicDTO = new ManagerTopicDTO();
            managerTopicDTO.setId(x.getId());
            managerTopicDTO.setCode(x.getCode());
            managerTopicDTO.setName(x.getName());
            managerTopicDTO.setStatusTopic(x.getStatusTopic());
            List<ManagerQuestionDTO> managerQuestionDTOS = new ArrayList<>();
            List<ManagerQuestion> questionList = x.getManagerQuestions().stream().filter(f ->
                     f.getStatusQuestion().equals(StatusFAQEnum.APPLY)).collect(Collectors.toList());
            questionList.forEach(q -> {
                ManagerQuestionDTO questionDTO = new ManagerQuestionDTO(q.getId(), q.getStatusQuestion(), true,
                        q.getQuestion(), q.getAnswer());
                managerQuestionDTOS.add(questionDTO);
            });
            managerTopicDTO.setManagerQuestionDTOS(managerQuestionDTOS);
            topicDTOS.add(managerTopicDTO);
        });
        return topicDTOS;
    }

    @Override
    public ManagerTopicDTO findOne(Long id) {
        ManagerTopicDTO managerTopicDTO = new ManagerTopicDTO();
        ManagerTopic managerTopic = managerTopicRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(ManagerTopicDTO.class,"Id",id.toString()));
        modelMapDTOTopic(managerTopic, managerTopicDTO);
        return managerTopicDTO;
    }

    @Override
    public ResponseDTO updatedTopic(Long id, ManagerTopicRequest managerTopicDTO) {
        ManagerTopic managerTopic = managerTopicRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(ManagerTopicDTO.class,"Id",id.toString()));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        managerTopic.setLastUpdatedBy(authentication.getPrincipal().toString());
        ManagerTopic managerTopicResponse = checkDataTopicDetail(managerTopic, managerTopicDTO);
        if (managerTopicResponse == null){
            throw new CustomException("Updated topic Error!!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseDTO("Updated topic !!!", "successfully!!!");
    }

    @Override
    public ResponseDTO createdTopic(ManagerTopicRequest managerTopicRequest) {
        ManagerTopic managerTopic = new ManagerTopic();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        managerTopic.setCreatedBy(authentication.getPrincipal().toString());
        ManagerTopic managerTopicResponse = checkDataTopicDetail(managerTopic, managerTopicRequest);
        if (managerTopicResponse == null){
            throw new CustomException("Created topic Error!!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseDTO("Created question !!!", "successfully!!!");
    }

    @Transactional
    public ManagerTopic checkDataTopicDetail(ManagerTopic managerTopic, ManagerTopicRequest managerTopicDTO) {
        if (managerTopicDTO.getName() != null && !managerTopicDTO.getName().isEmpty()) {
            managerTopic.setName(managerTopicDTO.getName());
        } else {
//            LOGGER.error("Check data Name topic null");
            throw new EntityNotFoundException(ManagerTopicDTO.class,"Name",managerTopicDTO.getName());
        }

        if (managerTopic.getCode() == null) {
            managerTopic.setCode("TP-"+managerTopicRepository.countManagerTopic());
        }

        if (managerTopicDTO.getStatusTopic() != null){
            managerTopic.setStatusTopic(managerTopicDTO.getStatusTopic());
        } else {
            throw new EntityNotFoundException(ManagerTopicDTO.class,"StatusTopic",managerTopicDTO.getStatusTopic().toString());
        }

        return managerTopicRepository.save(managerTopic);
    }

    @Override
    public ResponseDTO updateDeleteList(List<Long> ids) {
        if (ids.size() == 0) {
            throw new EntityNotFoundException("id",ids.toString());
        }
        List<ManagerTopic> managerTopics = managerTopicRepository.findAllById(ids);
        managerTopics.forEach(x -> x.setDeleted(true));
        managerTopicRepository.saveAll(managerTopics);
        return new ResponseDTO("Delete multipart topic","Successfully!");
    }

    public void modelMapDTOTopic(ManagerTopic model, ManagerTopicDTO dto){
        dto.setId(model.getId());
        dto.setCode(model.getCode());
        dto.setName(model.getName());
        dto.setStatusTopic(model.getStatusTopic());
    }

}
