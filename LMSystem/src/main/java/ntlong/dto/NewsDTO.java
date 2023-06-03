package ntlong.dto;

import ntlong.model.NewsContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewsDTO {

    private Long id;
    private String subject;
    private String textContent;
    private List<String> lmsNewsLabels = new ArrayList<>();
    private NewsContentType contentType;
    private Boolean status;
    private Boolean isHotNews;
    private Boolean isPinned;
    private Integer courseLink;
    private Integer eventLink;
    private Timestamp createdDate;
    private String attachmentLink;
    private String thumbnail;
    private String createdBy;
    private List<Long> userIds;

}
