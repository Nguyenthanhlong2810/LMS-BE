package ntlong.repository;

import ntlong.model.LmsNewsLabel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LmsNewsLabelRepository extends PagingAndSortingRepository<LmsNewsLabel, Long> {
    Boolean existsByLabel(String label);

    LmsNewsLabel findByLabel(String label);
}
