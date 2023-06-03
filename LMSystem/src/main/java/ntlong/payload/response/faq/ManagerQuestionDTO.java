package ntlong.payload.response.faq;

import ntlong.enums.StatusFAQEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManagerQuestionDTO {

    private Long id;
    private Timestamp createdDate;
    private String lastUpdatedBy;
    private Timestamp lastUpdated;
    private Boolean status;
    private String code;
    private String key;
    private String question;
    private String answer;
    private StatusFAQEnum statusQuestion;
    private String topicName;
    private ManagerTopicDTO managerTopic;


    public ManagerQuestionDTO(Long id, StatusFAQEnum statusQuestion, Boolean status, String question, String answer) {
        this.id = id;
        this.statusQuestion = statusQuestion;
        this.status = status;
        this.question = question;
        this.answer = answer;
    }
}
