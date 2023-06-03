package ntlong.service.impl.landingPage;

import com.google.gson.Gson;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ntlong.dto.FileDTO;
import ntlong.dto.UploadFileDTO;
import ntlong.dto.landingPage.FeedbackDTO;
import ntlong.dto.landingPage.LandingPageDTO;
import ntlong.dto.landingPage.TitleLandingPageDTO;
import ntlong.exception.CustomException;
import ntlong.exception.UploadFailException;
import ntlong.model.landingPage.Feedback;
import ntlong.model.landingPage.LandingPageSetting;
import ntlong.model.landingPage.TitleLandingPage;
import ntlong.payload.response.LandingPageResponse;
import ntlong.repository.landingPage.FeedbackRepository;
import ntlong.repository.landingPage.LandingPageSettingRepository;
import ntlong.repository.landingPage.TitleLandingPageRepository;
import ntlong.service.AmazonClient;
import ntlong.service.landingPage.LandingPageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@Service
public class LandingPageServiceImpl implements LandingPageService {



    private final AmazonClient amazonClient;

    private final TitleLandingPageRepository titleLandingPageRepository;

    private final FeedbackRepository feedbackRepository;

    private final LandingPageSettingRepository landingPageSettingRepository;

    private final ModelMapper modelMapper;

    private static final int MAXIMUM_SIZE_IMAGE = 5;

    private static final int PARAM_CONVERT_BYTES_TO_MB = 1024 * 1024;

    private static final int MINIMUM_WIDTH_IMAGE = 1440;

    private static final int MINIMUM_HEIGHT_IMAGE = 580;

    private final List<String> listFileType = Arrays.asList("png", "jpg", "jpeg");

    /**
     * get path prevew url of file
     * @param uploadFileDTO
     * @param fileName
     * @return
     */
    private String getUrl(UploadFileDTO uploadFileDTO, String fileName) {
        for (FileDTO fileDTO : uploadFileDTO.getFiles()) {
            if (fileName.equals(fileDTO.getFileName())) {
                return fileDTO.getPreviewUrl();
            }
        }
        return null;
    }

    /**
     * post new landing page
     * @param landingPageDTO
     * @return response
     * @throws IOException
     * @throws UploadFailException
     */
    @Override
    @Transactional
    public LandingPageSetting postLandingPageSetting(LandingPageDTO landingPageDTO) throws IOException, UploadFailException {
        //validate landing page

        UploadFileDTO uploadFileDTO;
        if(landingPageDTO.getImages() != null) {
            uploadFileDTO = amazonClient.uploadMultiFile( landingPageDTO.getImages());
            if(uploadFileDTO == null){
                throw new CustomException("Files không được để trống!",HttpStatus.BAD_REQUEST);
            }
        }else{
            throw new CustomException("Files không được để trống!",HttpStatus.BAD_REQUEST);
        }

        validateLandingPageDTO(landingPageDTO, uploadFileDTO);

        // create landing page
        LandingPageSetting landingPageSetting;
        LandingPageSetting existedLandingPage = null;
        List<LandingPageSetting> landingPageSettings = landingPageSettingRepository.findAll();
        if(landingPageSettings.size() > 0){
            existedLandingPage = landingPageSettingRepository.findAll().get(0);
        }
        //LandingPageSetting existedLandingPage = landingPageSettingRepository.findAll().get(0);

        if (existedLandingPage != null) {
            feedbackRepository.deleteAllByLandingPageSettingId(existedLandingPage.getId());
            titleLandingPageRepository.deleteAllByLandingPageSettingId(existedLandingPage.getId());
            landingPageSettingRepository.deleteAll();
        }
        landingPageSetting = new LandingPageSetting();

        // form link
        landingPageSetting.setFormLoginName(landingPageDTO.getFormLoginName());
        landingPageSetting.setFormLoginLink(getUrl(uploadFileDTO, landingPageDTO.getFormLoginName()));

        // system purpose link
        landingPageSetting.setSystemPurposeName(landingPageDTO.getSystemPurposeName());
        landingPageSetting.setSystemPurposeLink(getUrl(uploadFileDTO, landingPageDTO.getSystemPurposeName()));

        // introduce image
        landingPageSetting.setIntroduceImageName(landingPageDTO.getIntroduceImageName());
        landingPageSetting.setIntroduceImageLink(getUrl(uploadFileDTO, landingPageDTO.getIntroduceImageName()));

        LandingPageSetting savedLandingPage = landingPageSettingRepository.save(landingPageSetting);

        // title and description
        createTitleLandingPage(landingPageDTO, savedLandingPage);

        // feedbacks
        if(Objects.nonNull(landingPageDTO.getFeedbacks())){
            feedbackRepository.deleteAllByLandingPageSettingId(landingPageDTO.getId());
            for(String fb : landingPageDTO.getFeedbacks()){
                Gson g = new Gson();
                FeedbackDTO feedbackDTO = g.fromJson(fb, FeedbackDTO.class);
                Feedback feedback = new Feedback();
                if(getUrl(uploadFileDTO, feedbackDTO.getImageLearnerName()) == null){
                    throw new CustomException("Ảnh feedback học viên không được để trống!",HttpStatus.BAD_REQUEST);
                }
                feedback.setImageLearnerLink(getUrl(uploadFileDTO, feedbackDTO.getImageLearnerName()));
                feedback.setImageLearnerName(feedbackDTO.getImageLearnerName());
                feedback.setLearnerName(feedbackDTO.getLearnerName());
                feedback.setContentFeedback(feedbackDTO.getContentFeedback());
                feedback.setLandingPageSetting(savedLandingPage);
                feedbackRepository.save(feedback);
            }
        }
        return landingPageSettingRepository.save(landingPageSetting);
    }

    /**
     * create title landing page
     * @param landingPageDTO
     * @param savedLandingPage
     */
    private void createTitleLandingPage(LandingPageDTO landingPageDTO, LandingPageSetting savedLandingPage) {
        if(Objects.nonNull(landingPageDTO.getTitleLandingPages())){
            titleLandingPageRepository.deleteAllByLandingPageSettingId(savedLandingPage.getId());
            for(String title : landingPageDTO.getTitleLandingPages()){
                Gson g = new Gson();
                TitleLandingPage newTitleLandingPage = new TitleLandingPage();
                TitleLandingPage titleLandingPage = g.fromJson(title, TitleLandingPage.class);
                newTitleLandingPage.setTitle(titleLandingPage.getTitle());
                newTitleLandingPage.setDescription(titleLandingPage.getDescription());
                newTitleLandingPage.setLandingPageSetting(savedLandingPage);
                titleLandingPageRepository.save(newTitleLandingPage);
            }
        }
    }

    /**
     * get landing page by language
     * @return base response with LandingPageResponse
     */
    @Override
    public LandingPageResponse getLandingPage() {
        LandingPageResponse landingPageResponse = new LandingPageResponse();
        List<LandingPageSetting> landingPageSettings = landingPageSettingRepository.findAll();
        if(landingPageSettings.size() == 0){
            return landingPageResponse;
        }
        LandingPageSetting landingPageSetting = landingPageSettings.get(0);
        if(landingPageSetting == null){
            return landingPageResponse;
        }
        landingPageSetting.setFormLoginLink(landingPageSetting.getFormLoginLink());
        landingPageSetting.setSystemPurposeLink(landingPageSetting.getSystemPurposeLink());
        landingPageSetting.setIntroduceImageLink(landingPageSetting.getIntroduceImageLink());

        landingPageResponse.setLandingPageSetting(landingPageSetting);
        List<TitleLandingPage> titleLandingPages = titleLandingPageRepository
                .getTitleLandingPagesByLandingPageSettingId(landingPageSetting.getId());

        landingPageResponse.setTitleLandingPages(titleLandingPages.stream()
                .map(t -> modelMapper.map(t, TitleLandingPageDTO.class)).collect(Collectors.toList()));
        List<Feedback> feedbacks = feedbackRepository.getFeedbacksByLandingPageSettingId(landingPageSetting.getId());

        landingPageResponse.setFeedbacks(feedbacks.stream()
                .map(f -> modelMapper.map(f, FeedbackDTO.class)).collect(Collectors.toList()));

        landingPageResponse.getFeedbacks().forEach(f -> f.setImageLearnerLink(f.getImageLearnerLink()));
        return landingPageResponse;
    }

    /**
     * Update landing page
     * @param landingPageDTO
     * @return response
     * @throws IOException
     * @throws UploadFailException
     */
    @Override
    @Transactional
    public LandingPageSetting updateLandingPageSetting(LandingPageDTO landingPageDTO) throws IOException, UploadFailException {
        if(landingPageDTO.getId() == null){
            return null;
        }

        UploadFileDTO uploadFileDTO = null;
        if(landingPageDTO.getImages() != null) {
            uploadFileDTO = amazonClient.uploadMultiFile( landingPageDTO.getImages());
        }

        validateLandingPageDTO(landingPageDTO, uploadFileDTO);

        LandingPageSetting landingPageSetting = landingPageSettingRepository.getById(landingPageDTO.getId());

        // form link
        if(uploadFileDTO != null) {
            String formImageLink = getUrl(uploadFileDTO, landingPageDTO.getFormLoginName());
            landingPageSetting.setFormLoginLink(formImageLink != null ?
                    formImageLink : landingPageSetting.getFormLoginLink());
            landingPageSetting.setFormLoginName(landingPageDTO.getFormLoginName());

            String systemImageLink = getUrl(uploadFileDTO, landingPageDTO.getSystemPurposeName());
            landingPageSetting.setSystemPurposeName(landingPageDTO.getSystemPurposeName());
            landingPageSetting.setSystemPurposeLink(systemImageLink != null ?
                    systemImageLink : landingPageSetting.getSystemPurposeLink());

            String introduceImage1 = getUrl(uploadFileDTO, landingPageDTO.getIntroduceImageName());
            landingPageSetting.setIntroduceImageLink(introduceImage1 != null ?
                    introduceImage1 : landingPageSetting.getIntroduceImageLink());
            landingPageSetting.setIntroduceImageName(landingPageDTO.getIntroduceImageName());
        }

        landingPageSettingRepository.save(landingPageSetting);

        // title and description
        createTitleLandingPage(landingPageDTO, landingPageSetting);
        
        // feedbacks
        if(Objects.nonNull(landingPageDTO.getFeedbacks())){
            feedbackRepository.deleteAllByLandingPageSettingId(landingPageSetting.getId());
            for(String fb : landingPageDTO.getFeedbacks()){
                Gson g = new Gson();
                FeedbackDTO feedbackDTO = g.fromJson(fb, FeedbackDTO.class);
                Feedback feedback = new Feedback();
                String imageLearnerLink = null;
                if(uploadFileDTO != null) {
                    imageLearnerLink = getUrl(uploadFileDTO, feedbackDTO.getImageLearnerName());
                }

                feedback.setImageLearnerLink(imageLearnerLink != null
                        ? imageLearnerLink
                        : feedbackDTO.getImageLearnerLink());
                feedback.setImageLearnerName(feedbackDTO.getImageLearnerName());

                feedback.setLearnerName(feedbackDTO.getLearnerName());
                feedback.setContentFeedback(feedbackDTO.getContentFeedback());
                feedback.setLandingPageSetting(landingPageSetting);
                feedbackRepository.save(feedback);
            }
        }
        return landingPageSetting;
    }

    /**
     * Delete landing page by language
     * @return response
     */
    @Override
    public LandingPageSetting deleteLandingPage() {
        List<LandingPageSetting> landingPageSettings = landingPageSettingRepository.findAll();
        LandingPageSetting landingPageSetting = landingPageSettings.get(0);
        titleLandingPageRepository.deleteAllByLandingPageSettingId(landingPageSetting.getId());
        feedbackRepository.deleteAllByLandingPageSettingId(landingPageSetting.getId());
        landingPageSettingRepository.delete(landingPageSetting);

        return landingPageSetting;
    }

    //validate landing page
    private void validateLandingPageDTO(LandingPageDTO landingPageDTO, UploadFileDTO uploadFileDTO) throws UploadFailException {

        if(Objects.isNull(landingPageDTO.getFormLoginName())){
            throw new CustomException("Ảnh form đăng nhập không được để trống!", HttpStatus.BAD_REQUEST);
        }
        MultipartFile formLoginImage = getMultipartFile(landingPageDTO.getImages(), landingPageDTO.getFormLoginName());
        validateImageWithSize(formLoginImage);
        
        
        if(Objects.isNull(landingPageDTO.getSystemPurposeName())){
            throw new CustomException("Ảnh giới thiệu mục đích hệ thống không được để trống!", HttpStatus.BAD_REQUEST);
        }
        
        MultipartFile systemFile = getMultipartFile(landingPageDTO.getImages(), landingPageDTO.getSystemPurposeName());
        validateImageCapacity(systemFile);
        

        if(Objects.isNull(landingPageDTO.getIntroduceImageName())){
            throw new CustomException("Ảnh giới thiệu không được để trống!", HttpStatus.BAD_REQUEST);
        }
        
        MultipartFile introduceImage1 = getMultipartFile(landingPageDTO.getImages(), landingPageDTO.getIntroduceImageName());
        validateImageWithSize(introduceImage1);


        if(Objects.isNull(landingPageDTO.getTitleLandingPages())){
            throw new CustomException("Tiêu đề không được để trống!", HttpStatus.BAD_REQUEST);
        }
        for (String title : landingPageDTO.getTitleLandingPages()) {
            Gson g = new Gson();
            TitleLandingPage titleLandingPage = g.fromJson(title, TitleLandingPage.class);
            if (Objects.isNull(titleLandingPage.getTitle()) || Objects.isNull(titleLandingPage.getDescription())) {
                throw new CustomException("Tiêu đề không được để trống!", HttpStatus.BAD_REQUEST);
            }
        }
        
        
        if(Objects.isNull(landingPageDTO.getFeedbacks())){
            throw new CustomException("Feedback không được để trống!", HttpStatus.BAD_REQUEST);
        }
        for (String fb : landingPageDTO.getFeedbacks()) {
            Gson g = new Gson();
            FeedbackDTO feedbackDTO = g.fromJson(fb, FeedbackDTO.class);
            if (Objects.isNull(feedbackDTO.getImageLearnerName()) || Objects.isNull(feedbackDTO.getLearnerName())
                    || Objects.isNull(feedbackDTO.getContentFeedback())) {
                throw new CustomException("Ảnh feedback hoặc tên hoặc viên hoặc nội dung feedback không được để trống!", HttpStatus.BAD_REQUEST);
            }

            MultipartFile feedbackImage = getMultipartFile(landingPageDTO.getImages(), feedbackDTO.getImageLearnerName());
            validateImageWithSize(feedbackImage);
        }
        
    }

    private MultipartFile getMultipartFile(MultipartFile[] multipartFiles, String fileName){
        if(Objects.isNull(multipartFiles)){
            return null;
        }
        for (MultipartFile file : multipartFiles){
            if(file.getOriginalFilename().equals(fileName)){
                return file;
            }
        }
        return null;
    }

    //validate image size
    public void validateImageWithSize(MultipartFile attachment) throws UploadFailException {
        if(attachment == null){
            return;
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(attachment.getInputStream());

            if (bufferedImage.getWidth() < MINIMUM_WIDTH_IMAGE || bufferedImage.getHeight() < MINIMUM_HEIGHT_IMAGE) {
                throw new UploadFailException("Kích cỡ ảnh tối thiểu: 1440 x 580");
            }

            validateImageCapacity(attachment);

        } catch (IOException ex) {
            throw new UploadFailException("Không thể tải file!");
        }
    }

    //validate image capacity
    public void validateImageCapacity(MultipartFile attachment) throws UploadFailException {
        if(attachment == null){
            return;
        }
        String fileType = Objects.requireNonNull(attachment.getOriginalFilename())
                .split("\\.")[1]
                .toLowerCase();
        if (attachment.getSize() / PARAM_CONVERT_BYTES_TO_MB > MAXIMUM_SIZE_IMAGE
                || !listFileType.contains(fileType)) {
            throw new UploadFailException("Ảnh không đúng định dạng (png,jpg,jpeg) " +
                    "Kích cỡ ảnh vượt quá 5MB!");
        }
    }
}
