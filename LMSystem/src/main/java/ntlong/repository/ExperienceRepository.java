package ntlong.repository;

import ntlong.model.Experience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    @Query("select case when count(e) > 0 then true else false end from Experience e where e.name = :name")
    boolean existsByName(@Param("name") String name);
    @Query("select e from Experience e where e.name = :name and e.deleted = false ")
    Experience findByName(@Param("name") String name);
    @Query("select e from Experience e where (:name is null or :name = '' or e.name like %:name% and e.deleted = false )")
    Page<Experience> findByName(@Param("name") String name, Pageable pageable);

    boolean existsByNameAndDeletedFalse(@Param("name") String experienceName);

    @Query("select e from Experience e where e.name in :names and e.deleted = false ")
    List<Experience> getExperiencesByName(@Param("names") Set<String> names);

    @Query("select e.name from Experience e where e.deleted = false and e.name not in :experiences")
    List<String> getRemainingExperiences(@Param("experiences") Set<String> experiences);

    @Query(value = "update Experience e set e.deleted = true where e.name in :experiences")
    @Modifying
    @Transactional
    void updateDeletedExperiences(@Param("experiences") List<String> experiences);

    Page<Experience> findAllByDeletedFalse(Pageable pageable);

    Experience getExperienceByNameAndDeletedFalse(String name);

    @Query("select case when ( count(c) > 0) then true else false end from Course c inner join c.experiences ex " +
            "where c.deleted = false and ex.name = :expName")
    boolean existExperienceInCourse(@Param("expName") String experienceName);

    @Query("select case when ( count(au) > 0) then true else false end from AppUser au inner join au.experiences e " +
            "where e.name = :expName and e.deleted = false ")
    boolean existExperienceInUserExperiences(@Param("expName") String experienceName);
}
