package ntlong.service.impl;

import ntlong.dto.FileDTO;
import ntlong.dto.MyCertificateDTO;
import ntlong.enums.StatusCourseEnum;
import ntlong.exception.CustomException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.model.*;
import ntlong.payload.response.CertificateResponse;
import ntlong.repository.AssignCourseRepository;
import ntlong.repository.CourseRepository;
import ntlong.repository.CourseSettingRepository;
import ntlong.repository.UserRepository;
import ntlong.service.AmazonClient;
import ntlong.service.MyCertificateService;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.general.find.PdfTextFind;
import com.spire.pdf.general.find.PdfTextFindCollection;
import com.spire.pdf.graphics.*;
import com.spire.presentation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCertificateServiceImpl implements MyCertificateService {

    private final AssignCourseRepository assignCourseRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private final AmazonClient amazonClient;

    private final CourseSettingRepository courseSettingRepository;

    @Value("${api.upload.file.certificate}")
    private String myCertificateUploadPath;

    @Value("${api.upload.file.certificate-image}")
    private String myCertificateImagePath;

    private static final String IMAGE_FILE_TYPE = "PNG";
    private static final int LIMIT_COURSE_NAME_ONE_LINE = 55;

    private static final int MARGIN_TOP = 20;

    private static final String MY_CERTIFICATE_TEMPLATE_PPTX = "/template-pptx/Templates-Certificate-2.pptx";



    /**
     * Lấy ra danh sách chứng chỉ của học viên
     * <p>Danh sách chứa những bài học được hoàn thành bởi học viên và có cấp chứng chỉ</p>
     *
     * @param username
     * @param pageNo
     * @param pageSize
     * @param keyword
     * @return Page<MyCertificateDTO>
     */
    @Override
    public Page<MyCertificateDTO> getMyCertificatesFilter(String username, Integer pageNo, Integer pageSize, String keyword) {
        Long appUserId = getAppUserIdByUserName(username);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<MyCertificateDTO> myCertificateDTOPage = assignCourseRepository.findAllMyCertificates(appUserId, keyword, pageable);
        for(MyCertificateDTO myCertificateDTO : myCertificateDTOPage){
            CourseSetting courseSetting = courseSettingRepository.findByCourseId(myCertificateDTO.getCourseId());
            if(courseSetting != null){
                myCertificateDTO.setCertification(courseSetting.getIsCertificated());
            }
            myCertificateDTO.setCoursePathPreview(myCertificateDTO.getCoursePathPreview());
            myCertificateDTO.setMyCertificateImageLink(myCertificateDTO.getMyCertificateImageLink());
            myCertificateDTO.setMyCertificatePdfLink(myCertificateDTO.getMyCertificatePdfLink());
        }
        return myCertificateDTOPage;
    }

    /**
     * Lấy ra chi tiết chứng chỉ của học viên
     *
     * @param username
     * @param courseId
     * @return MyCertificateDTO
     */
    @Override
    public MyCertificateDTO findMyCertificateDetail(String username, Long courseId) throws Exception {
        Long appUserId = getAppUserIdByUserName(username);
        MyCertificateDTO myCertificateDTO = assignCourseRepository.findMyCertificateDetail(appUserId,courseId);
        CourseSetting courseSetting = courseSettingRepository.findByCourseId(courseId);
        if (myCertificateDTO == null)
            return null;
        if(courseSetting != null) {
            myCertificateDTO.setCertification(courseSetting.getIsCertificated());
        }
        Course course = courseRepository.findByIdAndDeletedFalse(courseId).orElseThrow(() -> new ResourceNotFoundException("Course is not exists!"));
        List<LessonStructure> lessonStructures = course.getLessonStructures();
        myCertificateDTO.setCourseDuration(getCourseDuration(lessonStructures));
        myCertificateDTO.setCourseContentTotal(getCourseContentTotal(lessonStructures));

        myCertificateDTO.setCoursePathPreview(convertLinkToDomain(myCertificateDTO.getCoursePathPreview()));

        myCertificateDTO.setMyCertificateImageLink(convertLinkToDomain(myCertificateDTO.getMyCertificateImageLink()));
        myCertificateDTO.setMyCertificatePdfLink(convertLinkToDomain(myCertificateDTO.getMyCertificatePdfLink()));
        return myCertificateDTO;
    }
    private String convertLinkToDomain(String linkFile){
        if(Objects.nonNull(linkFile) && !linkFile.isEmpty()) {
            return linkFile;
        }
        return null;
    }

    /**
     * Download file dựa vào type
     *
     * @param username
     * @param courseId
     * @param type
     * @param response
     * @throws IOException
     */
    @Override
    public void downloadMyCertificateDetailByType(String username, Long courseId, String type, HttpServletResponse response) throws IOException {
        Long appUserId = getAppUserIdByUserName(username);
        // Lấy thông tin chứng chỉ của tôi
        MyCertificateDTO myCertificateDTO = assignCourseRepository.findMyCertificateDetail(appUserId, courseId);
        CourseSetting courseSetting = courseSettingRepository.findByCourseId(courseId);
        String downloadPdfUrl = null,downloadImageUrl = null;
        if(courseSetting != null && courseSetting.getIsCertificated()) {
            downloadPdfUrl =  myCertificateDTO.getMyCertificatePdfLink();
            downloadImageUrl = myCertificateDTO.getMyCertificateImageLink();
        }

        // Nếu type = pdf thì lấy file pdf từ link pdf.Ngược lại nếu type = png/jpeg/jpg thì lấy file ảnh từ link ảnh
        String fileUrlSuffix = "PDF".equalsIgnoreCase(type) ? downloadPdfUrl : downloadImageUrl ;
        InputStream inputStream = getInputStreamOfPdfFile(fileUrlSuffix);

        InputStreamResource resource = new InputStreamResource(inputStream);
        switch (type) {
            case "jpg":
            case "jpeg":
                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                break;
            case "png":
                response.setContentType(MediaType.IMAGE_PNG_VALUE);
                break;
            case "gif":
                response.setContentType(MediaType.IMAGE_GIF_VALUE);
                break;
            default:
                break;
        }
        // Response file cho client
        StreamUtils.copy(resource.getInputStream(), response.getOutputStream());

    }

    /**
     *
     * @param username
     * @param courseIds
     * @param type
     * @param response
     */
    @Override
    public void downloadCertificatesAsZip(String username, List<Long> courseIds, String type, HttpServletResponse response) throws Exception {
        Long appUserId = getAppUserIdByUserName(username);
        InputStream inputStream;
        BufferedImage bufferedImage = null;
        Map<Long, URL> urlsPdf = new HashMap<>();
        if (courseIds == null) {
            return;
        }
        for (Long courseId : courseIds) {
            MyCertificateDTO certificateDTO = assignCourseRepository.findMyCertificateDetail(appUserId, courseId);
            CourseSetting courseSetting = courseSettingRepository.findByCourseId(courseId);
            String downloadPdfUrl = null,downloadImageUrl = null;
            if(courseSetting != null && courseSetting.getIsCertificated()) {
                downloadPdfUrl =  certificateDTO.getMyCertificatePdfLink();
                downloadImageUrl = certificateDTO.getMyCertificateImageLink();
            }
            String fileURLSuffix = "PDF".equalsIgnoreCase(type) ? downloadPdfUrl : downloadImageUrl;
            try {
                if(StringUtils.isBlank(fileURLSuffix)){
                    continue;
                }
                urlsPdf.put(certificateDTO.getCourseId(), new URL(fileURLSuffix));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ZipOutputStream zos = new ZipOutputStream(outputStream);
            for (Map.Entry<Long, URL> urlEntry : urlsPdf.entrySet()) {
                // Lấy file từ đường dẫn server
                inputStream = urlEntry.getValue().openStream();
                ZipEntry zipEntry = new ZipEntry(courseRepository.findByIdAndDeletedFalse(urlEntry.getKey())
                        .get().getName().concat(".").concat(type));
                zipEntry.setTime(System.currentTimeMillis());
                zos.putNextEntry(zipEntry);
                StreamUtils.copy(inputStream, zos);
            }
            zos.closeEntry();
            zos.finish();
        } catch (IOException | NullPointerException e ) {
            log.debug("Lỗi lấy file từ server!");
            log.error("ERROR:", e);
            e.printStackTrace();
        }
    }

    /**
     * Preview certificate
     * @param courseId
     * @param username
     * @return certificateResponse
     */
    @Override
    public CertificateResponse getPreviewCertificate(Long courseId, String username) {
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        AssignCourse assignCourse = assignCourseRepository.getDistinctFirstByAppUser_IdAndCourse_Id(appUser.getId(), courseId);
        CourseSetting courseSetting = courseSettingRepository.findByCourseId(courseId);
        if(Objects.isNull(assignCourse)){
            throw new ResourceNotFoundException("Không tìm thấy khóa học");
        }
        CertificateResponse certificateResponse = new CertificateResponse();
        String certificateUrl = assignCourse.getMyCertificateImageLink();
        String message = "Khóa học không có chứng chỉ";
        String messageTitle = "Chứng chỉ hoàn thành khóa học";
        if(courseSetting != null){
            if(courseSetting.getIsCertificated()){
                certificateUrl = assignCourse.getMyCertificateImageLink();
                message = "Bạn sẽ nhận được chứng chỉ sau khi hoàn thành toàn bộ nội dung khoá học";
                messageTitle = "Chứng chỉ";
            }
        }
        if(assignCourse.getProgressStatus().equals(StatusCourseEnum.COMPLETED)){
            certificateResponse.setCertificateLink(certificateUrl);
            certificateResponse.setMessageTitle(messageTitle);
        }else {
            certificateResponse.setMessage(message);
            certificateResponse.setMessageTitle(messageTitle);
        }
        return certificateResponse;
    }

    @Override
    public CertificateResponse createCertificate(Long courseId, String username) {
        CertificateResponse certificateResponse = new CertificateResponse();
        try {
            Long userId = userRepository.findIdByUsername(username);
            CourseSetting courseSetting = courseSettingRepository.findByCourseId(courseId);
            AssignCourse assignCourse = assignCourseRepository.getAssignCourseByCourseIdAndAppUserIdAndDeletedFalse(courseId, userId);
            if(!assignCourse.getProgressStatus().equals(StatusCourseEnum.COMPLETED)){
                certificateResponse.setMessage("Bạn chưa hoàn thành khóa học");
                return certificateResponse;
            }
            if (!courseSetting.getIsCertificated()) {
                certificateResponse.setMessage("Khoá học không cấp chứng chỉ");
                return certificateResponse;
            }
            log.info("creating certificate");
            long start = System.currentTimeMillis();
            log.debug("[updateCompletedCourse] creating certification start : {}", start);
            String pdfCertificateFileLink = generateAndUploadCertificate(userId, courseId, new Date(), MY_CERTIFICATE_TEMPLATE_PPTX);
            assignCourse.setMyCertificatePdfLink(pdfCertificateFileLink);
            String imageLink = generateMyCertificateImageLinkAndUploadImage(pdfCertificateFileLink);
            assignCourse.setMyCertificateImageLink(imageLink);
            assignCourseRepository.save(assignCourse);
            log.info("saved assign course after set certificate link");
            certificateResponse.setMessage("Tạo chứng chỉ thành công, truy cập Chứng chỉ để kiểm tra");
            certificateResponse.setCertificateLink(imageLink);
        }catch (Exception e){
            certificateResponse.setMessage("Tạo chứng chỉ không thành công");
        }
        return certificateResponse;
    }

    /**
     * Lấy inputstream của file pdf trên server theo link pdf
     * @param myCertificatePdfLink
     * @return InputStream
     */
    private InputStream getInputStreamOfPdfFile(String myCertificatePdfLink) {
        URL url = null;
        InputStream inputStream = null;
        BufferedImage bufferedImage = null;
        try {
            // Lấy file từ đường dẫn server
            url = new URL(myCertificatePdfLink);
            inputStream = url.openStream();
        } catch (MalformedURLException e) {
            log.debug("Lỗi lấy file từ server!");
            log.error("ERROR:", e);
            e.printStackTrace();
        } catch (IOException e) {
            log.error("ERROR:", e);
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * Lấy ra thời lượng khóa học = Tổng thời lượng mỗi nội dung trong khóa học
     *
     * @param lessonStructures
     * @return long
     */
    private long getCourseDuration(List<LessonStructure> lessonStructures) {
        long courseDuration = 0L;
        for (LessonStructure lessonStructure : lessonStructures) {
            courseDuration += lessonStructure.getLessonContentUploads().stream().mapToLong(LessonContentUpload::getDuration).sum();
        }
        return courseDuration;
    }

    /**
     * Lấy ra tổng số bài giảng của khóa học = Tổng số nội dung có trong khóa học
     *
     * @param lessonStructures
     * @return long
     */
    private long getCourseContentTotal(List<LessonStructure> lessonStructures) {
        return lessonStructures.size();
    }

    /**
     * Lấy user id theo username
     *
     * @param username
     * @return
     */
    private Long getAppUserIdByUserName(String username) {
        AppUser appUser = userRepository.findByUsernameAndEnabledTrueAndDeletedFalse(username);
        return appUser.getId();
    }

    /**
     * Chuyển file pptx sang pdf và lưu vào server
     *
     * @param userId
     * @param courseId
     * @return pdfFileName
     * @throws Exception
     */
    public String generateAndUploadCertificate(Long userId, Long courseId, Date completedDate, String templateUrl) throws Exception {
        log.info("generating certificate");
        MyCertificateDTO myCertificateDTO = assignCourseRepository.createMyCertificateDTO(userId, courseId);
        //generate certificate from pptx
        Presentation presentation = new Presentation(MyCertificateServiceImpl.class.
                                                     getResourceAsStream(templateUrl),
                                                     FileFormat.AUTO);
        ISlide slide = presentation.getSlides().get(0);

        Map<String, String> map = new HashMap<>();

        String courseName = WordUtils.wrap(myCertificateDTO.getCourseName().trim(), LIMIT_COURSE_NAME_ONE_LINE);

        map.put("#managerName#", "NGUYEN THANH LONG");
        map.put("#position#", "LEARNING AND DEVELOPMENT MANAGER");

        replaceText(slide, map);

        String username = myCertificateDTO.getUsername();

        String fullUsername = myCertificateDTO.getUserFullName();
        if(Objects.isNull(fullUsername)){
            throw new CustomException("Tên người dùng không có giá trị", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // Chuyển pptx thành pdf

        presentation.saveToFile(myCertificateUploadPath + username+".pdf", FileFormat.PDF);

        String dateStr = generateFormatDateCertificate(completedDate);
        drawTextCertificate(myCertificateUploadPath + username+".pdf", username, fullUsername,
                courseName,dateStr);
        drawSuffixDate(myCertificateUploadPath + username+".pdf",username, completedDate, dateStr);
        log.info("added username and completed date");
        //upload file to server and return preview link
        File file = new File(myCertificateUploadPath + username+".pdf");
        FileDTO fileDTO = amazonClient.uploadFile(file);
        file.delete();
        log.info("uploaded certificate to server");
        return fileDTO.getPreviewUrl();
    }

    private int countLines(String courseName){
        String[] lines = courseName.split("\r\n|\r|\n");
        return lines.length;
    }

    private String generateFormatDateCertificate(Date completedDate){
        String pattern = "dd MMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        StringBuilder sb = new StringBuilder(simpleDateFormat.format(completedDate));
        sb.insert(2,"OF");
        sb.insert(2,"   ");
        //int day = Integer.parseInt(sb.substring(0,2));
//        switch (day){
//            case 1:
//                sb.insert(sb.indexOf(" "), "st OF");
//                break;
//            case 2:
//                sb.insert(sb.indexOf(" "), "nd OF");
//                break;
//            case 3:
//                sb.insert(sb.indexOf(" "), "rd OF");
//                break;
//            default:
//                sb.insert(sb.indexOf(" "), "th OF");
//                break;
//        }
        sb.insert(0,"ON THE ");
        return sb.toString().toUpperCase();
    }

    private String getSuffixDate(Date completedDate){
        String pattern = "dd MMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        StringBuilder sb = new StringBuilder(simpleDateFormat.format(completedDate));
        int day = Integer.parseInt(sb.substring(0,2));
        switch (day){
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    private void drawTextCertificate(String fileName,String username, String fullname, String courseName, String date){

        //Create a PdfDocument object
        PdfDocument doc = new PdfDocument();

        //Load a sample PDF document
        doc.loadFromFile(fileName);

        //Get the first page
        PdfPageBase page = doc.getPages().get(0);

        //Create a PdfTrueTypeFont object based on a specific used font
        PdfTrueTypeFont fontFile = new PdfTrueTypeFont(MyCertificateServiceImpl.class
                .getResourceAsStream("/font/SVN-Beauford.otf"), 48);
        log.info("create font beauford successfully");
        //calculate y offset of text
        PdfTextFindCollection findResults2 = page
                .findText("HAS SUCCESSFULLY COMPLETED", true, false);

        PdfTextFind findCollection2 = findResults2.getFinds()[0];
        double offsetY2 = findCollection2.getTextBounds().get(0).getMinY();
        double offsetY = offsetY2 - 58;

        PdfStringFormat centerAlignment = new PdfStringFormat(PdfTextAlignment.Center);
        
        PdfRGBColor customColor = new PdfRGBColor(0,32,96);
        PdfBrush pdfBrush = new PdfSolidBrush(customColor);
        
        PdfStringFormat centerBoth = new PdfStringFormat(PdfTextAlignment.Center, PdfVerticalAlignment.Middle);
        page.getCanvas().drawString(fullname, fontFile, pdfBrush,
                page.getCanvas().getClientSize().getWidth() / 2, offsetY,centerBoth);

        PdfTrueTypeFont fontJosefinSansSemiBold = new PdfTrueTypeFont(MyCertificateServiceImpl.class
                .getResourceAsStream("/font/JosefinSans-SemiBold.ttf"), 20);
        log.info("create font fontJosefinSansSemiBold successfully");

        PdfTrueTypeFont fontJosefinSans = new PdfTrueTypeFont(MyCertificateServiceImpl.class
                .getResourceAsStream("/font/JosefinSans-Regular.ttf"), 12);
        log.info("create font fontJosefinSans successfully");

        customColor = new PdfRGBColor(43,40,32);
        pdfBrush = new PdfSolidBrush(customColor);

        double offsetCourseName = offsetY2 + 24;
        page.getCanvas().drawString(courseName, fontJosefinSansSemiBold, pdfBrush,
                page.getCanvas().getClientSize().getWidth() / 2, offsetCourseName,centerAlignment);

        double dateOffset = offsetY2 + 33;

        int numLines = countLines(courseName);

        page.getCanvas().drawString(date, fontJosefinSans, pdfBrush,
                page.getCanvas().getClientSize().getWidth() / 2, dateOffset + MARGIN_TOP * numLines,centerAlignment);
        //Save to file
        doc.saveToFile(myCertificateUploadPath+username+".pdf");
    }

    private void drawSuffixDate(String fileName,String username, Date completedDate, String date){
        PdfStringFormat centerAlignment = new PdfStringFormat(PdfTextAlignment.Center);

        PdfRGBColor customColor = new PdfRGBColor(43,40,32);
        PdfBrush pdfBrush = new PdfSolidBrush(customColor);

        //Create a PdfDocument object
        PdfDocument doc = new PdfDocument();

        //Load a sample PDF document
        doc.loadFromFile(fileName);

        //Get the first page
        PdfPageBase page = doc.getPages().get(0);

        PdfTrueTypeFont fontJosefinSansSmall = new PdfTrueTypeFont(MyCertificateServiceImpl.class
                .getResourceAsStream("/font/JosefinSans-Regular.ttf"), 9);
        String day = date.substring(0,9);

        PdfTextFindCollection findDayText = page
                .findText(day, true, true);

        PdfTextFind dayCollection = findDayText.getFinds()[0];
        double offsetXDay = dayCollection.getTextBounds().get(0).getMaxX() + 4;
        double offsetYDay = dayCollection.getTextBounds().get(0).getMinY();
        String suffixDay = getSuffixDate(completedDate);
        page.getCanvas().drawString(suffixDay, fontJosefinSansSmall, pdfBrush,
                offsetXDay, offsetYDay - 2, centerAlignment);

        doc.saveToFile(myCertificateUploadPath+username+".pdf");
    }
    /**
     * Chuyển từ file pdf sang file image(png)
     * <p>Upload file ảnh lên server</p>
     * <p>Return lại đường dẫn của file</p>
     * @param pdfFileLink
     * @return fileDTO.getPreviewUrl()
     * @throws IOException
     */
    public String generateMyCertificateImageLinkAndUploadImage(String pdfFileLink) throws IOException {
        // Load file pdf from server
        InputStream inputStream = getInputStreamOfPdfFile(pdfFileLink);
        // Convert to image
        BufferedImage bufferedImage = null;
        PdfDocument pdf = new PdfDocument(inputStream);
        for (int i = 0; i < pdf.getPages().getCount(); i++) {
            bufferedImage = pdf.saveAsImage(i, PdfImageType.Bitmap);
        }
        File file = new File(myCertificateImagePath);
        ImageIO.write(bufferedImage, IMAGE_FILE_TYPE, file);
        FileDTO fileDTO = amazonClient.uploadFile(file);
        return fileDTO.getPreviewUrl();
    }

    /**
     * Thay thế text trong power point template
     *
     * @param slide
     * @param map
     */
    private void replaceText(ISlide slide, Map<String, String> map) {
        for (Object shape : slide.getShapes()) {
            if (shape instanceof IAutoShape) {
                for (Object paragraph : ((IAutoShape) shape).getTextFrame().getParagraphs()) {
                    ParagraphEx paragraphEx = (ParagraphEx) paragraph;
                    for (String key : map.keySet()) {
                        if (paragraphEx.getText().contains(key)) {
                            paragraphEx.setText(paragraphEx.getText().replace(key, map.get(key)));
                        }
                    }
                }
            }
        }
    }
}
