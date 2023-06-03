package ntlong.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import ntlong.dto.UploadFileDTO;
import ntlong.dto.banner.BannerDTO;
import ntlong.dto.banner.BannerResponse;
import ntlong.dto.banner.BannerType;
import ntlong.dto.banner.BannerUpdateDTO;
import ntlong.exception.CustomException;
import ntlong.exception.ResourceNotFoundException;
import ntlong.exception.UploadFailException;
import ntlong.model.Banner;
import ntlong.model.Thumbnail;
import ntlong.repository.BannerRepository;
import ntlong.repository.ThumbnailRepository;
import ntlong.service.AmazonClient;
import ntlong.service.BannerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BannerServiceImpl implements BannerService {

    private final AmazonClient amazonClient;

    private final BannerRepository bannerRepository;

    private final ThumbnailRepository thumbnailRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Banner createBanner(BannerDTO bannerDTO) throws IOException {
        thumbnailRepository.deleteAll();
        bannerRepository.deleteAll();
        Banner banner = new Banner();
        if (Objects.nonNull(bannerDTO.getAttachmentLink())) {
            MultipartFile[] files = new MultipartFile[1];
            files[0] = bannerDTO.getAttachmentLink();
            UploadFileDTO uploadFileDTO = amazonClient.uploadMultiFile(files);
            banner.setAttachmentName(uploadFileDTO.getFiles().get(0).getFileName());
            banner.setAttachmentLink(uploadFileDTO.getFiles().get(0).getPreviewUrl());
        }
        banner.setType(bannerDTO.getType());
        banner.setImgTime(bannerDTO.getImgTime());
        Banner savedBanner = bannerRepository.save(banner);
        List<Thumbnail> thumbnails = new ArrayList<>();
        if (bannerDTO.getType().equals(BannerType.IMAGE)) {
            if (Objects.nonNull(bannerDTO.getThumbnails()) && bannerDTO.getType().equals(BannerType.IMAGE)) {
                UploadFileDTO uploadFileDTO = amazonClient.uploadMultiFile(bannerDTO.getThumbnails());
                if (Objects.isNull(uploadFileDTO)) {
                    throw new CustomException("Upload thumbnail files null", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
                }
                for (int i = 0; i < uploadFileDTO.getFiles().size(); i++) {
                    Thumbnail thumbnail = new Thumbnail();
                    thumbnail.setThumbnailName(uploadFileDTO.getFiles().get(i).getFileName());
                    thumbnail.setThumbnailLink(uploadFileDTO.getFiles().get(i).getPreviewUrl());
                    thumbnail.setBanner(savedBanner);
                    thumbnails.add(thumbnailRepository.save(thumbnail));
                }
            }
        }
        return savedBanner;
    }

    @Override
    @Transactional
    public Banner updateBanner(BannerUpdateDTO bannerUpdateDTO) throws ResourceNotFoundException, UploadFailException, IOException {
        Banner banner = bannerRepository.getBanner();
        if (banner == null) {
            throw new ResourceNotFoundException("Banner does not existed");
        }
        Banner oldBanner = new Banner();
        thumbnailRepository.deleteAll();
        bannerRepository.deleteAll();
        if (Objects.nonNull(bannerUpdateDTO.getAttachmentLink())) {
            Object attachment = bannerUpdateDTO.getAttachmentLink();
            if(attachment instanceof MultipartFile) {
                MultipartFile[] files = new MultipartFile[1];
                files[0] = (MultipartFile) bannerUpdateDTO.getAttachmentLink();
                UploadFileDTO uploadFileDTO = amazonClient.uploadMultiFile(files);
                oldBanner.setAttachmentName(uploadFileDTO.getFiles().get(0).getFileName());
                oldBanner.setAttachmentLink(uploadFileDTO.getFiles().get(0).getPreviewUrl());
            }else{
                oldBanner.setAttachmentName(banner.getAttachmentName());
                oldBanner.setAttachmentLink(banner.getAttachmentLink());
            }
        }
        oldBanner.setId(banner.getId());
        oldBanner.setImgTime(bannerUpdateDTO.getImgTime());
        oldBanner.setType(bannerUpdateDTO.getType());
        Banner savedBanner = bannerRepository.save(oldBanner);
        if (bannerUpdateDTO.getType().equals(BannerType.IMAGE)) {
            if(!Objects.isNull(bannerUpdateDTO.getThumbnails())) {
                Object[] thumbnails = bannerUpdateDTO.getThumbnails();
                for (Object obj : thumbnails) {
                    if (obj instanceof MultipartFile) {
                        MultipartFile[] files = new MultipartFile[1];
                        files[0] = (MultipartFile) obj;
                        UploadFileDTO uploadFileDTO = amazonClient.uploadMultiFile(files);
                        Thumbnail thumbnail = new Thumbnail();
                        thumbnail.setThumbnailName(uploadFileDTO.getFiles().get(0).getFileName());
                        thumbnail.setThumbnailLink(uploadFileDTO.getFiles().get(0).getPreviewUrl());
                        thumbnail.setBanner(savedBanner);
                        thumbnailRepository.save(thumbnail);
                    } else {
                        Gson g = new Gson();
                        Thumbnail thumbnail = g.fromJson(obj.toString(), Thumbnail.class);
                        if (Objects.isNull(thumbnail.getThumbnailLink()) || Objects.isNull(thumbnail.getThumbnailName())) {
                            continue;
                        }
                        thumbnail.setThumbnailLink(thumbnail.getThumbnailLink());
                        thumbnail.setBanner(savedBanner);
                        thumbnailRepository.save(thumbnail);
                    }
                }
            }
        }
        return savedBanner;
    }

    @Override
    public BannerResponse getBanner(BannerType bannerType) {
        Banner banner = bannerRepository.getBanner();
        if(Objects.isNull(banner)){
            return new BannerResponse();
        }
        if (Objects.nonNull(bannerType)) {
            if (banner.getType().equals(bannerType)) {
                return createBannerResponse(banner);
            } else {
                return new BannerResponse();
            }
        } else {
            return createBannerResponse(banner);
        }
    }

    @Override
    public void deleteBanner() {
        thumbnailRepository.deleteAll();
        bannerRepository.deleteAll();
    }

    private BannerResponse createBannerResponse(Banner banner){
        banner.setAttachmentLink(banner.getAttachmentLink());
        List<Thumbnail> thumbnails = thumbnailRepository.findThumbnailsByBannerId(banner.getId());
        thumbnails.forEach(tb -> tb.setThumbnailLink(tb.getThumbnailLink()));
        BannerResponse bannerResponse = modelMapper.map(banner,BannerResponse.class);
        bannerResponse.setThumbnails(thumbnails);
        return bannerResponse;
    }


}
