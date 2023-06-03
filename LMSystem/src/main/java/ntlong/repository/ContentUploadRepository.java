package ntlong.repository;

import ntlong.enums.TypeContentUploadEnum;
import ntlong.model.ContentUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentUploadRepository extends JpaRepository<ContentUpload, Long> {
    @Query("select c from ContentUpload c where " +
            "(:keySearch is null or :keySearch = '' or unaccent(lower(c.nameContent)) like unaccent(concat('%', lower(trim(:keySearch)) ,'%'))) " +
            "and (c.type =:type or :type ='' or :type is null) ")
    Page<ContentUpload> searchContentUpload(Pageable pageable, String keySearch, TypeContentUploadEnum type);

}
