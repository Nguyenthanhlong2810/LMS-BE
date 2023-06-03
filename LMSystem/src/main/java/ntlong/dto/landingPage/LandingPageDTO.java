package ntlong.dto.landingPage;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LandingPageDTO {

    private Long id;

    private String formLoginName;

    private String systemPurposeName;

    private String introduceImageName;

    private List<String> feedbacks;

    private MultipartFile[] images;

    private List<String> titleLandingPages;

}
