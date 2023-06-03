package ntlong.model;


import ntlong.enums.TypeContentUploadEnum;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Table(name = "content_upload")
@AllArgsConstructor
@Getter
@Setter
public class ContentUpload extends BaseEntity {

    @Column(name = "name_content", nullable = false)
    private String nameContent; //Tên nội dung

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeContentUploadEnum type; //Loại nội dung

    @Column(name = "time_long", nullable = false)
    private String timeLong; //Thời lượng

    @Column(name = "linkFileContent", nullable = false)
    private String linkFileContent; //Link file nội dung

    @Column(name = "sort_order")
    private long sortOrder;
}
