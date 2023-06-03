package ntlong.repository.faq;

import ntlong.enums.StatusFAQEnum;
import ntlong.model.faq.ManagerQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by txtrung
 * Date: 08/06/2022
 * Time: 15:20
 * Project name: lms-faq
 */

@Repository
public interface ManagerQuestionRepository extends JpaRepository<ManagerQuestion, Long> {


    @Query(value ="select distinct q from ManagerQuestion q " +
            " where (q.deleted = false or q.deleted is null) " +
            "and (:searchValue is null or :searchValue = '' or unaccent(lower(q.lastUpdatedBy)) like unaccent(concat('%',lower(trim(:searchValue)) ,'%'))" +
            " or unaccent(lower(q.question)) like unaccent(concat('%',lower(trim(:searchValue)) ,'%'))) " +
            "and (:status is null or q.statusQuestion = :status ) " +
            "and (:topicId is null or q.managerTopic.id = :topicId )" +
            " order by q.createdDate desc")
    Page<ManagerQuestion> findAllBySearch(@Param("searchValue") String searchValue,
                                          @Param("status") StatusFAQEnum status,
                                          @Param("topicId") Long topicId,
                                          Pageable pageable);

    @Query(value = "select count(t.id) as countTopic from ManagerQuestion t")
    Long countManagerQuestion();
}
