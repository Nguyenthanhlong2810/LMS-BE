package ntlong.controller.faq;

import ntlong.payload.request.faq.ManagerTermsRequest;
import ntlong.service.faq.ManagerTermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/terms")
public class TermsController {

    private final ManagerTermsService managerTopicService;

    @Autowired
    public TermsController(ManagerTermsService managerTopicService) {
        this.managerTopicService = managerTopicService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTerms(){
        return new ResponseEntity<>(managerTopicService.getAllTerms(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTermsById(@PathVariable Long id){
        return new ResponseEntity<>(managerTopicService.findOne(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createdTerms(@RequestBody ManagerTermsRequest request){
        return new ResponseEntity<>(managerTopicService.createdTerms(request), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatedTerms(@RequestBody ManagerTermsRequest request, @PathVariable Long id){
        return new ResponseEntity<>(managerTopicService.updateTerms(id, request), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTerms(@RequestBody List<Long> ids){
        return new ResponseEntity<>(managerTopicService.updateDeleteListTerms(ids), HttpStatus.OK);
    }
}
