package ntlong.repository.faq;

import ntlong.model.faq.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ManagerContactInfoRepository extends JpaRepository<ContactInfo, Long> {

    List<ContactInfo> findAllByDeletedFalse();

    @Modifying
    @Transactional
    @Query("delete from ContactInfo ci")
    void deleteAll();


}
