package ntlong.repository;

import ntlong.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByName(String name);
    Tag findByName(String name);
    // Pending for bad mapping relation
//    Set<Tag> findAllByCourseId(Long courseId);
}
