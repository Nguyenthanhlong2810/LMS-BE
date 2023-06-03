package ntlong.controller.faq;

import ntlong.enums.StatusFAQEnum;
import ntlong.exception.faq.EntityNotFoundException;
import ntlong.payload.request.faq.ManagerTopicRequest;
import ntlong.service.faq.ManagerTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by txtrung
 * Date: 06/06/2022
 * Time: 15:47
 * Project name: lms-faq
 */

@RestController
@RequestMapping("/topic")
public class TopicController {

    private final ManagerTopicService managerTopicService;

    @Autowired
    public TopicController(ManagerTopicService managerTopicService) {
        this.managerTopicService = managerTopicService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTopics(@RequestParam(value = "offset", required = false, defaultValue = "1") Integer offset,
                                          @RequestParam(value = "limit", required = false, defaultValue = "25") Integer limit,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "status", required = false) StatusFAQEnum status
                                              ) throws EntityNotFoundException {
        Pageable pageable = PageRequest.of(offset -1, limit);
        return new ResponseEntity<>(managerTopicService.topicSearch(name,status,pageable), HttpStatus.OK);
    }


    @GetMapping("/question")
    public ResponseEntity<?> getAllTopicQuestions(@RequestParam(value = "searchKeys", required = false) String searchValues) throws EntityNotFoundException{
        return new ResponseEntity<>(managerTopicService.topicQuestionAll(searchValues), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createdTopic(@RequestBody ManagerTopicRequest managerTopicDTO){
        return new ResponseEntity<>(managerTopicService.createdTopic(managerTopicDTO), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatedTopic(@RequestBody ManagerTopicRequest managerTopicDTO, @PathVariable Long id){
        return new ResponseEntity<>(managerTopicService.updatedTopic(id, managerTopicDTO), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicById(@PathVariable Long id){
        return new ResponseEntity<>(managerTopicService.findOne(id), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTopic(@RequestBody List<Long> ids){
        return new ResponseEntity<>(managerTopicService.updateDeleteList(ids), HttpStatus.OK);
    }

}
