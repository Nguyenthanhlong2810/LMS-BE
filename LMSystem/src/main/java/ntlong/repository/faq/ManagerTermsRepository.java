package ntlong.repository.faq;

import ntlong.model.faq.ManagerTerms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerTermsRepository extends JpaRepository<ManagerTerms, Long> {

    List<ManagerTerms> findAllByDeletedFalse();

}
