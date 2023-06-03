package ntlong.model;

import ntlong.dto.banner.BannerType;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "banner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "attachment_name")
    private String attachmentName;

    @Column(name = "attachment_link")
    private String attachmentLink;

    @Column(name = "img_time")
    private int imgTime;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private BannerType type;
}
