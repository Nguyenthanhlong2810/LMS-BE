package ntlong.dto.banner;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannerDTO {
    private Long id;
    private int imgTime;
    private MultipartFile attachmentLink;
    private MultipartFile[] thumbnails;
    private BannerType type;
}

