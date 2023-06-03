package ntlong.repository;

import ntlong.model.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThumbnailRepository extends JpaRepository<Thumbnail,Long> {
    List<Thumbnail> findThumbnailsByBannerId(Long bannerId);
}
