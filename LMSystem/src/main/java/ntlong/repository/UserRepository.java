package ntlong.repository;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ntlong.model.AppUser;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

  boolean existsByUsernameAndDeletedFalseAndEnabledTrue(String username);

  boolean existsByEmailAndDeletedFalseAndEnabledTrue(String email);

  AppUser findByUsernameAndEnabledTrueAndDeletedFalse(String username);

  AppUser findByUsernameAndEnabledFalseAndDeletedFalse(String username);

  Optional<AppUser> findByIdAndEnabledTrueAndDeletedFalse(Long userId);

  @Query("select a.id from AppUser a where a.username = :username")
  Long findIdByUsername(String username);


  @Transactional
  void deleteByUsername(String username);

  @Query(value = "select au from AppUser au where au.deleted = false  and au.enabled = true " +
          "and (:name is null or :name = '' or upper(au.fullname) like upper(concat('%',:name,'%')) " +
          "or upper(au.username) like upper(concat('%',:name,'%'))) ")
  Page<AppUser> getList(Pageable pageable,String name);
}
