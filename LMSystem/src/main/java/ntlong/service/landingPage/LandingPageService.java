package ntlong.service.landingPage;

import ntlong.dto.landingPage.LandingPageDTO;
import ntlong.exception.UploadFailException;
import ntlong.model.landingPage.LandingPageSetting;
import ntlong.payload.response.LandingPageResponse;

import java.io.IOException;

public interface LandingPageService {
    LandingPageSetting postLandingPageSetting(LandingPageDTO landingPageDTO) throws IOException, UploadFailException;

    LandingPageResponse getLandingPage();

    LandingPageSetting updateLandingPageSetting(LandingPageDTO landingPageDTO) throws IOException, UploadFailException;

    LandingPageSetting deleteLandingPage();
}
