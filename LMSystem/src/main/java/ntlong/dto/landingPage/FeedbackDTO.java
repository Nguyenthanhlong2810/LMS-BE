package ntlong.dto.landingPage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {

    private String imageLearnerLink;

    private String imageLearnerName;

    private String learnerName;

    private String contentFeedback;
}
