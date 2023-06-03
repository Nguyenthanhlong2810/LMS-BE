package ntlong.payload.request.faq;

import ntlong.enums.StatusFAQEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ManagerTopicRequest {
    private String key;
    private String name;
    private StatusFAQEnum statusTopic;

}
