package ntlong.controller.faq;

import lombok.AllArgsConstructor;
import ntlong.enums.StatusFAQEnum;
import ntlong.payload.request.faq.ManagerQuestionRequest;
import ntlong.service.faq.ManagerQuestionService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by txtrung
 * Date: 08/06/2022
 * Time: 17:21
 * Project name: lms-faq
 */

@RestController
@RequestMapping("/question")
@AllArgsConstructor
public class QuestionController {

    private final ManagerQuestionService managerQuestionService;


    @GetMapping
    public ResponseEntity<?> getAllQuestion(@RequestParam(value = "offset", required = false, defaultValue = "1") Integer offset,
                                            @RequestParam(value = "limit", required = false, defaultValue = "25") Integer limit,
                                            @RequestParam(value = "searchValues", required = false) String searchValues,
                                            @RequestParam(value = "status", required = false) StatusFAQEnum status,
                                            @RequestParam(value = "topicId", required = false) Long topicId){
        Pageable pageable = PageRequest.of(offset -1, limit);
        return new ResponseEntity<>(managerQuestionService.questionSearch(searchValues, status,topicId, pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id){
        return new ResponseEntity<>(managerQuestionService.findOne(id), HttpStatus.OK);
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createdQuestion(@RequestPart(required = false) MultipartFile[] file,
                                             @RequestPart String questionRequestJson) throws IOException {
        Gson g = new Gson();
        ManagerQuestionRequest questionRequest = g.fromJson(questionRequestJson, ManagerQuestionRequest.class);
        return new ResponseEntity<>(managerQuestionService.createQuestion(file, questionRequest), HttpStatus.OK);
    }

    @PutMapping(consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updatedQuestion(@RequestPart(required = false) MultipartFile[] file,
                                             @RequestPart String questionRequestJson,
                                             @RequestPart String id) throws IOException {
        Gson g = new Gson();
        ManagerQuestionRequest questionRequest = g.fromJson(questionRequestJson, ManagerQuestionRequest.class);
        return new ResponseEntity<>(managerQuestionService.updateQuestion(file, questionRequest, Long.valueOf(id)), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteQuestion(@RequestBody List<Long> ids){
        return new ResponseEntity<>(managerQuestionService.updateDeleteList(ids), HttpStatus.OK);
    }

}
