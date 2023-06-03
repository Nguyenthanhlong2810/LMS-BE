package ntlong.service.impl;

import ntlong.dto.NewsDTO;
import ntlong.exception.NewsNotExistsException;
import ntlong.exception.UploadFailException;
import ntlong.model.AssignCourse;
import ntlong.model.LmsNews;
import ntlong.model.LmsNewsLabel;
import ntlong.model.NewsContentType;
import ntlong.repository.AssignCourseRepository;
import ntlong.repository.LmsNewsLabelRepository;
import ntlong.repository.LmsNewsRepository;
import ntlong.service.AmazonClient;
import ntlong.service.LmsNewsService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LmsNewsServiceImpl implements LmsNewsService {


    private static final int LMS_NEWS_PINED_LIMIT = 10;

    private static final int MAXIMUM_SIZE_IMAGE = 15;

    private static final int PARAM_CONVERT_BYTES_TO_MB = 1024 * 1024;

    private static final int MINIMUM_WIDTH_IMAGE = 1440;

    private static final int MINIMUM_HEIGHT_IMAGE = 580;

    private final List<String> listFileType = Arrays.asList("png", "jpg", "jpeg");

    private final List<String> listVideoType = Arrays.asList("mp4");

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LmsNewsRepository lmsNewsRepository;

    @Autowired
    private LmsNewsLabelRepository lmsNewsLabelRepository;

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private AssignCourseRepository assignCourseRepository;

    @Override
    @Transactional
    public LmsNews createNews(NewsDTO newsDTO, MultipartFile attachment, MultipartFile thumbnail) throws UploadFailException {
        LmsNews news = new LmsNews();
        BeanUtils.copyProperties(newsDTO, news);

        for (String label : newsDTO.getLmsNewsLabels()) {
            if (Boolean.FALSE.equals(lmsNewsLabelRepository.existsByLabel(label))) {
                lmsNewsLabelRepository.save(new LmsNewsLabel(label));
            }
            news.getLmsNewsLabels().add(lmsNewsLabelRepository.findByLabel(label));
        }

        if (Objects.nonNull(attachment)) {
            validateFile(attachment, newsDTO.getContentType());
            try {
                //upload file
                news.setAttachmentLink(amazonClient.uploadFile(attachment).getPreviewUrl());
                //upload thumbnail
                if (Objects.nonNull(thumbnail)) {
                    news.setThumbnail(amazonClient.uploadFile(thumbnail).getPreviewUrl());
                }
            } catch (Exception ex) {
                throw new UploadFailException("Could not upload file");
            }
        }

        return lmsNewsRepository.save(news);
    }

    @Override
    @Transactional
    public LmsNews updateNews(NewsDTO newsDTO, MultipartFile attachment, MultipartFile thumbnail) throws NewsNotExistsException, UploadFailException {

        LmsNews lmsNews = lmsNewsRepository.findById(newsDTO.getId())
                .orElseThrow(() -> new NewsNotExistsException("news does not existed"));

        // Can not pin over 10 news that with status true
        if (newsDTO.getIsPinned() != null && newsDTO.getIsPinned() && lmsNews.getIsPinned() != null && !lmsNews.getIsPinned()) {
            int totalLmsNewsPined = lmsNewsRepository.countLmsNewsByStatusIsTrueAndIsPinnedIsTrue();
            if(totalLmsNewsPined >= LMS_NEWS_PINED_LIMIT) {
                throw new UploadFailException("Không thể ghim hơn 10 tin có trạng thái là Đã đăng");
            }
            if (!lmsNews.getStatus()) {
                throw new UploadFailException("Không thể ghim tin có trạng thái là Bản nháp");
            }
        }

        BeanUtils.copyProperties(newsDTO, lmsNews);

        //upload file
        if (Objects.nonNull(attachment)) {
            validateFile(attachment, newsDTO.getContentType());
            try {
                lmsNews.setAttachmentLink(amazonClient.uploadFile( attachment).getPreviewUrl());
            } catch (Exception ex) {
                throw new UploadFailException("Could not upload file");
            }
        }

        //upload thumbnail
        if (Objects.nonNull(thumbnail)) {
            try {
                lmsNews.setThumbnail(amazonClient.uploadFile( thumbnail).getPreviewUrl());
            } catch (Exception ex) {
                throw new UploadFailException("Could not upload file");
            }
        }

        Set<LmsNewsLabel> lmsNewsLabelList = new HashSet<>();
        for (String label : newsDTO.getLmsNewsLabels()) {
            if (Boolean.FALSE.equals(lmsNewsLabelRepository.existsByLabel(label))) {
                lmsNewsLabelRepository.save(new LmsNewsLabel(label));
            }
            lmsNewsLabelList.add(lmsNewsLabelRepository.findByLabel(label));
        }
        lmsNews.setLmsNewsLabels(lmsNewsLabelList);
        if(StringUtils.isNotBlank(lmsNews.getAttachmentLink()))
        lmsNews.setAttachmentLink(lmsNews.getAttachmentLink());

        if(StringUtils.isNotBlank(lmsNews.getThumbnail()))
        lmsNews.setThumbnail(lmsNews.getThumbnail());
        return lmsNewsRepository.save(lmsNews);
    }

    @Override
    public void deleteNews(Long id) throws NewsNotExistsException {
        if (!lmsNewsRepository.existsById(id)) {
            throw new NewsNotExistsException("news does not existed");
        }
        lmsNewsRepository.deleteById(id);
    }

    @Override
    public void deleteNews(Long[] ids) throws NewsNotExistsException {

        for (long id : ids) {
            if (!lmsNewsRepository.existsById(id)) {
                throw new NewsNotExistsException("news does not existed");
            }
        }

        lmsNewsRepository.deleteAllById(Arrays.asList(ids));
    }

    @Override
    public NewsDTO findById(Long id) {
        LmsNews lmsNews = lmsNewsRepository.findById(id).orElseThrow(() -> new NullPointerException("News does not existed"));
        NewsDTO lmsNewsDTO = modelMapper.map(lmsNews, NewsDTO.class);
        if (lmsNews.getCourseLink() != null) {
            List<AssignCourse> assignCourses = assignCourseRepository.findAssignCoursesByCourseId(lmsNews.getCourseLink().longValue());
            List<Long> userIds = assignCourses.stream().map(item -> item.getAppUser().getId()).collect(Collectors.toList());
            lmsNewsDTO.setUserIds(userIds);
        }
        return lmsNewsDTO;
    }

    @Override
    public Page<NewsDTO> getListNews(String keyword,
                                     NewsContentType contentType,
                                     Boolean status,
                                     Boolean isPinned,
                                     Integer page,
                                     Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LmsNews> lmsNewsPage = lmsNewsRepository.getListNews(keyword, contentType, status,isPinned, pageable);

        Page<NewsDTO> dtoPage = lmsNewsPage.map(item -> {
            NewsDTO newsDTO = new NewsDTO();
            BeanUtils.copyProperties(item, newsDTO);
            for (LmsNewsLabel lmsNewsLabel : item.getLmsNewsLabels()) {
                newsDTO.getLmsNewsLabels().add(lmsNewsLabel.getLabel());
            }
            return newsDTO;
        });
        return dtoPage;
    }

    @Override
    public Page<LmsNews> getListHotNews(Integer page, Integer size, boolean status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<LmsNews> lmsNewsList = lmsNewsRepository.getListHotNews(pageable, status).toList();
        if (!lmsNewsList.isEmpty()) {
            Page<LmsNews> lmsNews = lmsNewsRepository.getListHotNews(pageable, status);
            return lmsNews;
        } else {
            Page<LmsNews> lmsNews = lmsNewsRepository.findFirstByStatusIsTrueOrderByCreatedDate(pageable);
            return lmsNews;
        }
    }

    private void validateFile(MultipartFile attachment, NewsContentType type) throws UploadFailException {

        String [] fileTypeArr = Objects.requireNonNull(attachment.getOriginalFilename())
                .split("\\.");
        String fileType = fileTypeArr[fileTypeArr.length - 1]
                .toLowerCase();

        if(type.equals(NewsContentType.VIDEO)){
            if(!listVideoType.contains(fileType)){
                throw new UploadFailException("Hệ thống chỉ hỗ trợ định dạng "+ listVideoType);
            }
            if((attachment.getSize() / (float) PARAM_CONVERT_BYTES_TO_MB > 200)){
                throw new UploadFailException("Dung lượng video vượt quá 200MB");
            }
            return;
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(attachment.getInputStream());

            if(listVideoType.contains(fileType)){
                if((attachment.getSize() / (float) PARAM_CONVERT_BYTES_TO_MB > 200)){
                    throw new UploadFailException("Hệ thống chỉ hỗ trợ định dạng mp4, tối đa 200MB");
                }
                return;
            }

            if (bufferedImage.getWidth() < MINIMUM_WIDTH_IMAGE || bufferedImage.getHeight() < MINIMUM_HEIGHT_IMAGE) {
                throw new UploadFailException("Kích cỡ ảnh tối thiểu: 1440 x 580");
            }

            if (attachment.getSize() / (float) PARAM_CONVERT_BYTES_TO_MB > MAXIMUM_SIZE_IMAGE
                    || !listFileType.contains(fileType)) {
                throw new UploadFailException("Định dạng ảnh không hợp lệ (png,jpg,jpeg). " +
                        "Dung lượng ảnh tối đa là 15MB ");
            }
        } catch (IOException ex) {
            throw new UploadFailException("Không thể upload tệp tin");
        }
    }


}
