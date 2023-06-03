package ntlong.payload.request.faq;

import ntlong.enums.StatusFAQEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ManagerQuestionRequest {

    private String key;
    private String question;
    private String answer;
    private String typeFile;
    private String urlFile;
    private StatusFAQEnum statusQuestion;
    private Long managerTopicId;


}
