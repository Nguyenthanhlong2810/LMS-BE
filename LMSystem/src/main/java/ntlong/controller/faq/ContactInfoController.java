package ntlong.controller.faq;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ntlong.payload.request.faq.ContactInfoRequest;
import ntlong.service.faq.ManagerContactInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contactInfo")
@AllArgsConstructor
public class ContactInfoController {

    private final ManagerContactInfoService managerContactInfoService;

    @GetMapping
    public ResponseEntity<?> getAllContactInfo(){
        return new ResponseEntity<>(managerContactInfoService.getContactInfo(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContactInfoById(@PathVariable Long id){
        return new ResponseEntity<>(managerContactInfoService.findOne(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createdContactInfo(@RequestBody ContactInfoRequest request){
        return new ResponseEntity<>(managerContactInfoService.createdContactInfo(request), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatedContactInfo(@RequestBody ContactInfoRequest request, @PathVariable Long id){
        return new ResponseEntity<>(managerContactInfoService.updateContactInfo(id, request), HttpStatus.OK);
    }

}
