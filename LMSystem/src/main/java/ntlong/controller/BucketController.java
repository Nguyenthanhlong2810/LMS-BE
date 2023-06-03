package ntlong.controller;

import ntlong.response.BaseResponse;
import ntlong.service.AmazonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/storage/")
public class BucketController {
    private AmazonClient amazonClient;

    @Autowired
    BucketController(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<BaseResponse> uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return new ResponseEntity<>(new BaseResponse(HttpStatus.OK.value(),
                "Upload file success",this.amazonClient.uploadFile(file)),HttpStatus.OK);
    }
}
