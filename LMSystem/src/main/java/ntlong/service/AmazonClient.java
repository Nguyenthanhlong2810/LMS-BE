package ntlong.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import ntlong.dto.FileDTO;
import ntlong.dto.UploadFileDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AmazonClient {
    private AmazonS3 s3client;

    private static final String DOMAIN = "https://my-lms-project.s3.ap-northeast-1.amazonaws.com/";

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials);
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private String generateFileName(File file) {
        return new Date().getTime() + "-" + file.getName().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
    public FileDTO uploadFile(File file) {
        String fileNameS3 = generateFileName(file);
        String fileName = file.getName();
        String fileUrl = "";
        try {
            fileUrl = DOMAIN + fileNameS3;
            uploadFileTos3bucket(fileNameS3, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileDTO fileDTO = new FileDTO(fileName,fileUrl);
        return fileDTO;
    }

    public FileDTO uploadFile(MultipartFile multipartFile) {
        String fileNameS3 = generateFileName(multipartFile);
        String fileName = multipartFile.getOriginalFilename();
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            fileUrl = DOMAIN + fileNameS3;
            uploadFileTos3bucket(fileNameS3, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileDTO fileDTO = new FileDTO(fileName,fileUrl);
        return fileDTO;
    }

    public UploadFileDTO uploadMultiFile(MultipartFile[] multipartFiles){
        List<FileDTO> fileDTOList = new ArrayList<>();
        for(MultipartFile file : multipartFiles){
            FileDTO uploadedFile = uploadFile(file);
            fileDTOList.add(uploadedFile);
        }
        return new UploadFileDTO(multipartFiles.length, fileDTOList);
    }

}
