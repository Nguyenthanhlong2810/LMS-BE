package ntlong.utils;

import ntlong.dto.FileDTO;
import ntlong.exception.faq.CustomException;
import ntlong.model.DefaultPreviewImage;
import ntlong.repository.PreviewImageRepository;
import lombok.RequiredArgsConstructor;
import ntlong.service.AmazonClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommonImpl implements Common{


    private final AmazonClient amazonClient;
    private final PreviewImageRepository previewImageRepository;

    private static final String IMAGE_PREVIEW = "/images/preview-image-1.jpg";
    @Override
    public List<Long> extractIdFromString(String idString) {
        List<Long> result = StringUtils.isEmpty(idString) ? null : Arrays.asList(idString.split(","))
                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        return result;
    }

    public DefaultPreviewImage createPreviewImage() {
        try {
            File file = new File(Objects.requireNonNull(getClass().getResource(IMAGE_PREVIEW)).getFile());
            FileDTO fileDTO = amazonClient.uploadFile(file);
            DefaultPreviewImage defaultPreviewImage = new DefaultPreviewImage(file.getName(),fileDTO.getPreviewUrl());
            return previewImageRepository.save(defaultPreviewImage);
        } catch (NullPointerException nullPointerException){
            throw new ntlong.exception.CustomException("Không tìm thấy ảnh mặc định của khóa học!", HttpStatus.NOT_FOUND);
        }
    }

    public DefaultPreviewImage getPreviewImage(){
        List<DefaultPreviewImage> previewImages = previewImageRepository.getDefaultPreviewImages();
        if(previewImages.isEmpty()){
            return null;
        }
        return previewImageRepository.getDefaultPreviewImages().get(0);
    }

    public String generateTransactionId(Long userId){
        return "LMS"+System.currentTimeMillis()+userId;
    }

}
