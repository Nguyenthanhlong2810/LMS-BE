package ntlong.repository;

import ntlong.model.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByNameAndDeletedFalse(String name);

    Skill findByNameAndDeletedFalse(String name);

    @Query("SELECT t FROM Skill t where t.deleted = false ")
    Page<Skill> findAllWithPagination(Pageable pageable);


    @Query("select s from Skill s where s.name in :names and s.deleted = false ")
    List<Skill> getSkillsByName(@Param("names") Set<String> names);

    @Query("select s.name from Skill s where s.deleted = false and s.adminCreated = true and s.name not in :skills")
    List<String> getRemainingSkills(@Param("skills") Set<String> skills);

    @Query(value = "update Skill t set t.deleted = true where t.name in :skills")
    @Modifying
    @Transactional
    void updateDeletedSkills(@Param("skills") List<String> skills);

    Page<Skill> findAllByDeletedFalse(Pageable pageable);

    @Query("select case when ( count(c) > 0) then true else false end from Course c join c.skills s " +
            "where c.deleted = false and s.name = :removeSkill")
    boolean existSkillInCourse(String removeSkill);


    @Query("select case when ( count(au) > 0) then true else false end from AppUser au join au.skillsInteresting s " +
            "where s.name = :removeSkill and s.deleted = false ")
    boolean existSkillInUserSkills(String removeSkill);

}
