package ntlong.repository.faq;
import ntlong.enums.StatusFAQEnum;
import ntlong.model.faq.ManagerTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by txtrung
 * Date: 06/06/2022
 * Time: 16:19
 * Project name: lms
 */

@Repository
public interface ManagerTopicRepository extends JpaRepository<ManagerTopic, Long> {

    @Query(value ="select distinct t from ManagerTopic t" +
            " where (t.deleted = false or t.deleted is null) " +
            "and (:name is null or :name = '' or unaccent(lower(t.name)) like unaccent(lower(concat('%',:name,'%')))) " +
            "and (:status is null or t.statusTopic = :status ) order by t.createdDate desc")
    Page<ManagerTopic> findAllBySearch(@Param("name") String name,
                                       @Param("status") StatusFAQEnum status,
                                       Pageable pageable);
    @Query(value ="select distinct t from ManagerTopic t left join ManagerQuestion q on t.id = q.managerTopic.id " +
            "where t.statusTopic = 'APPLY' and (q.statusQuestion = 'APPLY' or q.statusQuestion is null or q.statusQuestion = 'NOT_APPLY')" +
            "and (lower(t.name) like lower(concat('%',trim(:searchValues),'%')) " +
            "or lower(q.question) like lower(concat('%',trim(:searchValues),'%')) or :searchValues is null or :searchValues = '') " +
            "order by t.createdDate desc")
    List<ManagerTopic> findAllBySearchParams(@Param("searchValues") String searchValues);

    @Query(value = "select count(t.id) as countTopic from ManagerTopic t")
    Long countManagerTopic();

}
