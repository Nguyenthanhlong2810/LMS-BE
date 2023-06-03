package ntlong.dto.landingPage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LandingPageUpdateDTO {
    private Long id;

    private String formLoginName;

    private String systemPurposeName;

    private String introduceImageName1;

    private String introduceImageName2;

    private String language;

    private List<Object> feedbacks;

    private List<Object> titleLandingPages;
}
