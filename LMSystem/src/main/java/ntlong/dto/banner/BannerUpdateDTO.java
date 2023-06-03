package ntlong.dto.banner;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannerUpdateDTO {
    private Long id;
    private int imgTime;
    private String attachmentName;
    private Object attachmentLink;
    private Object[] thumbnails;
    private BannerType type;
}
