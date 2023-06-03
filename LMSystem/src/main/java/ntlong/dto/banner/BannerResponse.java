package ntlong.dto.banner;

import ntlong.model.Thumbnail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BannerResponse {

    private Long id;

    private String attachmentName;

    private String attachmentLink;

    private int imgTime;

    private BannerType type;

    private List<Thumbnail> thumbnails;
}
