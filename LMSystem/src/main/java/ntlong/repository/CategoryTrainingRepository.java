package ntlong.repository;

import ntlong.model.CategoryTraining;
import ntlong.payload.response.categorytraining.MenuInternalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CategoryTrainingRepository extends JpaRepository<CategoryTraining, Long> {
    @Transactional
    @Modifying
    @Query(value = "update CategoryTraining c set c.deleted = true where c.id = :id")
    void updateIsDeleted(@Param("id") long id);

    @Query(value = "select c from CategoryTraining c where " +
            "c.deleted = false " +
            "order by c.createdDate desc ")
    List<CategoryTraining> getAllListCategoryTraining();

    @Query(value = "select c from CategoryTraining c where " +
            "(:name is null or :name = '' or unaccent(lower(c.name)) like unaccent(lower(concat('%',:name,'%')))) " +
            "and c.deleted = false " +
            "order by c.createdDate desc, c.no ")
    Page<CategoryTraining> getListCategoryTrainingFilter(Pageable pageable, @Param("name") String name);
    CategoryTraining findByName(String name);

    boolean existsByName(String name);

    @Query("select c from CategoryTraining c where c.deleted = false and c.no =:no and c.parent =:parent")
    Optional<CategoryTraining> findNoByParent(@Param("no") int no,@Param("parent") long parent);

    @Query("select c from CategoryTraining c where (lower(c.name)  like lower(concat('%', :name ,'%')) or :name is null or :name ='') " +
            "and c.parent = 0 and c.deleted=false")
    List<CategoryTraining> findCategoryTrainingByName(@Param("name") String name);

    @Query("select c.name from CategoryTraining c where c.id =:parent")
    String findByParent(@Param("parent") long parent);

    @Query("select new ntlong.payload.response.categorytraining.MenuInternalResponse(c.id,c.name,c.parent,c.no)" +
            " from CategoryTraining c where c.parent =:parentId and c.deleted = false order by c.createdDate desc ")
    List<MenuInternalResponse> getMenuByParentId(Long parentId);
}
