package ntlong.repository;

import ntlong.model.RequirementCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequirementCourseRepository extends JpaRepository<RequirementCourse, Long> {
    boolean existsByName(String name);

    RequirementCourse findByName(String name);
}
