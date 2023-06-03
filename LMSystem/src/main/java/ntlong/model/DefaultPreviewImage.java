package ntlong.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "default_preview_image")
public class DefaultPreviewImage extends BaseEntity {

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_link")
    private String imageLink;
}
