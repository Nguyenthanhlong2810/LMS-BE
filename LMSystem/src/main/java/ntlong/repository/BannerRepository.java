package ntlong.repository;

import ntlong.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner,Long> {
    @Query(value="SELECT * FROM Banner LIMIT 1", nativeQuery = true)
    Banner getBanner();
}
