package ntlong.payload.response.faq;

import lombok.*;
import ntlong.enums.StatusFAQEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
public class ManagerTopicDTO {

    private Long id;
    private String code;
    private Timestamp createdDate;
    private String lastUpdatedBy;
    private Timestamp lastUpdated;
    private Boolean status;
    private String name;
    private StatusFAQEnum statusTopic;

    private List<ManagerQuestionDTO> managerQuestionDTOS;

    public ManagerTopicDTO(Long id, String code, String name, StatusFAQEnum statusTopic) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.statusTopic = statusTopic;
    }

    public ManagerTopicDTO(Long id, String code,String name, StatusFAQEnum statusTopic, List<ManagerQuestionDTO> managerQuestionDTOS) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.statusTopic = statusTopic;
        this.managerQuestionDTOS = managerQuestionDTOS;
    }
}
