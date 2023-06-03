package ntlong.repository;

import ntlong.model.LmsNews;
import ntlong.model.NewsContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LmsNewsRepository extends PagingAndSortingRepository<LmsNews, Long> {

    @Query("select distinct t " +
            "from LmsNews t left join t.lmsNewsLabels nl " +
            "where (:isPinned is null or (:isPinned is not null and t.isPinned = true)) " +
            "and (unaccent(lower(t.subject)) like unaccent(lower(concat('%',trim(:keyword),'%'))) " +
            "or unaccent(lower(t.textContent)) like unaccent(lower(concat('%',trim(:keyword),'%'))) " +
            " or unaccent(lower(nl.label)) like unaccent(lower(concat('%',trim(:keyword),'%')))) " +
            "and (:status is not null and t.status = :status " +
            "or (:status is null and ( t.status is null or t.status = true or t.status = false ))) " +
            "and (:contentType is null or t.contentType = :contentType) " +
            "order by t.createdDate desc ")
    Page<LmsNews> getListNews(String keyword, NewsContentType contentType, Boolean status, Boolean isPinned, Pageable pageable);


    @Query("select t " +
            "from LmsNews t " +
            "where t.isHotNews = true and t.status = :status " +
            "order by t.lastUpdated desc ")
    Page<LmsNews> getListHotNews(Pageable pageable, boolean status);

    Page<LmsNews> findFirstByStatusIsTrueOrderByCreatedDate(Pageable pageable);

    int countLmsNewsByStatusIsTrueAndIsPinnedIsTrue();
}
