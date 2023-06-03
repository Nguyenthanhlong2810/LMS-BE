package ntlong.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "thumbnail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Thumbnail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "thumbnail_link")
    private String thumbnailLink;

    @Column(name = "thumbnail_name")
    private String thumbnailName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference
    private Banner banner;
}
