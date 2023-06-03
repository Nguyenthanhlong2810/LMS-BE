package ntlong.service.faq.impl;

import lombok.AllArgsConstructor;
import ntlong.dto.UploadFileDTO;
import ntlong.enums.StatusFAQEnum;
import ntlong.exception.faq.CustomException;
import ntlong.exception.faq.EntityNotFoundException;
import ntlong.model.faq.ManagerQuestion;
import ntlong.model.faq.ManagerTopic;
import ntlong.payload.request.faq.ManagerQuestionRequest;
import ntlong.payload.response.faq.ManagerQuestionDTO;
import ntlong.payload.response.faq.ManagerTopicDTO;
import ntlong.payload.response.faq.ResponseDTO;
import ntlong.repository.faq.ManagerQuestionRepository;
import ntlong.repository.faq.ManagerTopicRepository;
import ntlong.service.AmazonClient;
import ntlong.service.faq.ManagerQuestionService;
import ntlong.utils.Common;
import org.modelmapper.ModelMapper;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by txtrung
 * Date: 08/06/2022
 * Time: 14:19
 * Project name: lms-faq
 */

@Service
@AllArgsConstructor
public class ManagerQuestionServiceImpl implements ManagerQuestionService {


    private final ManagerQuestionRepository managerQuestionRepository;
    private final ManagerTopicRepository managerTopicRepository;
    
    private final Common common;
    private final ModelMapper modelMapper;

    @Override
    public Page<ManagerQuestionDTO> questionSearch(String searchValues, StatusFAQEnum status,Long topicId, Pageable pageable) {
        Page<ManagerQuestion> managerQuestions = managerQuestionRepository.findAllBySearch(searchValues,status,topicId, pageable);
        List<ManagerQuestionDTO> managerQuestionDTOS = new ArrayList<>();
        managerQuestions.getContent().forEach(x ->  {
           ManagerQuestionDTO questionDTO = modelMapper.map(x, ManagerQuestionDTO.class);
           managerQuestionDTOS.add(questionDTO);
        });
        return new PageImpl<>(managerQuestionDTOS, pageable, managerQuestions.getTotalElements());
    }

    @Override
    public ManagerQuestionDTO findOne(Long id) {
        ManagerQuestionDTO dto = new ManagerQuestionDTO();
        ManagerQuestion managerQuestion = managerQuestionRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(ManagerQuestionDTO.class,"Id",id.toString()));
        modelMapDTOTopic(managerQuestion,dto);
        return dto;
    }

    @Override
    public ResponseDTO createQuestion(MultipartFile[] file, ManagerQuestionRequest questionRequest) throws IOException {
        if (questionRequest.getManagerTopicId() == null) {
            throw new EntityNotFoundException(ManagerQuestionRequest.class,"idTopic",questionRequest.getManagerTopicId().toString());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ManagerQuestion managerQuestion = new ManagerQuestion();
        ManagerTopic managerTopic = managerTopicRepository.findById(questionRequest.getManagerTopicId()).orElseThrow(() ->
                new EntityNotFoundException(ManagerTopicDTO.class,"Id",questionRequest.getManagerTopicId().toString()));
        managerQuestion.setManagerTopic(managerTopic);
        managerQuestion.setCreatedBy(authentication.getPrincipal().toString());
        managerQuestion.setLastUpdatedBy(authentication.getPrincipal().toString());
        ManagerQuestion question = checkDataQuestionDetail(managerQuestion, questionRequest);
        if (question == null){
            throw new CustomException("Create question Error!!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseDTO("Create question !!!", "successfully!!!");
    }

    @Override
    public ResponseDTO updateQuestion(MultipartFile[] file, ManagerQuestionRequest questionRequest, Long id) throws IOException {
        ManagerQuestion managerQuestion = managerQuestionRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(ManagerQuestionDTO.class,"Id",id.toString()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        managerQuestion.setLastUpdatedBy(authentication.getPrincipal().toString());
        if (!Objects.equals(questionRequest.getManagerTopicId(), managerQuestion.getManagerTopic().getId())){
            ManagerTopic managerTopic = managerTopicRepository.findById(questionRequest.getManagerTopicId()).orElseThrow(() ->
                    new EntityNotFoundException(ManagerTopicDTO.class,"Id",questionRequest.getManagerTopicId().toString()));
            managerQuestion.setManagerTopic(managerTopic);
        }
        ManagerQuestion question = checkDataQuestionDetail(managerQuestion, questionRequest);
        if (question == null){
            throw new CustomException("Update question Error!!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseDTO("Update question !!!", "successfully!!!");
    }

    @Transactional
    public ManagerQuestion checkDataQuestionDetail(ManagerQuestion model, ManagerQuestionRequest request) {

        if (request.getQuestion() != null && !request.getQuestion().isEmpty()) {
            model.setQuestion(request.getQuestion());
        } else {
//            LOGGER.error("Check data question for topic null!!!");
            throw new EntityNotFoundException(ManagerQuestionRequest.class,"question",request.getQuestion());
        }

        if (request.getAnswer() != null && !request.getAnswer().isEmpty()) {
            model.setAnswer(request.getAnswer());
        } else {
//            LOGGER.error("Check data answer for topic null!!!");
            throw new EntityNotFoundException(ManagerQuestionRequest.class,"answer",request.getAnswer());
        }

        if (model.getCode() == null) {
            model.setCode("QA-"+managerQuestionRepository.countManagerQuestion());
        }

        if (request.getStatusQuestion() != null){
            model.setStatusQuestion(request.getStatusQuestion());
        } else {
//            LOGGER.error("Check data status topic null");
            throw new EntityNotFoundException(ManagerQuestionRequest.class,"StatusQuestion&Answer",request.getStatusQuestion().toString());
        }
        return managerQuestionRepository.save(model);
    }

    @Override
    public ResponseDTO updateDeleteList(List<Long> ids) {
        if (ids.size() == 0) {
            throw new EntityNotFoundException("id",ids.toString());
        }
        List<ManagerQuestion> managerTopics = managerQuestionRepository.findAllById(ids);
        managerTopics.forEach(x -> {x.setDeleted(true);});
        managerQuestionRepository.saveAll(managerTopics);
        return new ResponseDTO("Delete list question!", "Successfully!");
    }

    public void modelMapDTOTopic(ManagerQuestion model, ManagerQuestionDTO dto){
        dto.setId(model.getId());
        dto.setLastUpdatedBy(model.getLastUpdatedBy());
        dto.setCode(model.getCode());
        dto.setAnswer(model.getAnswer());
        dto.setQuestion(model.getQuestion());
        dto.setStatusQuestion(model.getStatusQuestion());
        dto.setTopicName(model.getManagerTopic().getName());
    }
}
