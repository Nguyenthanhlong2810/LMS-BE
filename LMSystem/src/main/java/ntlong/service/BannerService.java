package ntlong.service;


import ntlong.dto.banner.BannerDTO;
import ntlong.dto.banner.BannerResponse;
import ntlong.dto.banner.BannerType;
import ntlong.dto.banner.BannerUpdateDTO;
import ntlong.exception.ResourceNotFoundException;
import ntlong.exception.UploadFailException;
import ntlong.model.Banner;

import java.io.IOException;

public interface BannerService {
    Banner createBanner(BannerDTO bannerDTO) throws UploadFailException, IOException;

    Banner updateBanner(BannerUpdateDTO bannerDTO) throws ResourceNotFoundException, UploadFailException, IOException;

    BannerResponse getBanner(BannerType bannerType);

    void deleteBanner();
}
