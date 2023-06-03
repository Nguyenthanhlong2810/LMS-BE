package ntlong.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CategoryTrainingResponseDTO {
    private long id;
    private String name;
    private long parent;
    private String parentName;
    private int no;
    public String createdBy;
    public Timestamp createdDate;
    private String title;
    private String description;
}
