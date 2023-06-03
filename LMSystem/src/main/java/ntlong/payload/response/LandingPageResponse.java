package ntlong.payload.response;

import ntlong.dto.landingPage.FeedbackDTO;
import ntlong.dto.landingPage.TitleLandingPageDTO;
import ntlong.model.landingPage.LandingPageSetting;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class LandingPageResponse {
    private LandingPageSetting landingPageSetting;
    private List<TitleLandingPageDTO> titleLandingPages;
    private List<FeedbackDTO> feedbacks;
}
