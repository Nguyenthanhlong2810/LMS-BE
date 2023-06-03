package ntlong.repository;

import ntlong.model.DefaultPreviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreviewImageRepository extends JpaRepository<DefaultPreviewImage, Long> {

    @Query(value="SELECT dpi FROM DefaultPreviewImage dpi order by dpi.createdDate desc")
    List<DefaultPreviewImage> getDefaultPreviewImages();
}
