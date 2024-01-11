package br.com.desafio.UserFieldVisibility.repository;

import br.com.desafio.UserFieldVisibility.entity.UserFieldVisibility;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFieldVisibilityRepository extends JpaRepository<UserFieldVisibility, Long> {


    @Query(nativeQuery = true, value = "UPDATE tbl_user_visibility SET user_visibility_is_visible = ?2 where user_visibility_field_name = ?1")
    @Modifying
    @Transactional
    void changeVisibilityFieldForUser(String field, boolean isVisible);

    @Query(nativeQuery = true, value = "SELECT user_visibility_field_name FROM tbl_user_visibility WHERE user_visibility_field_name =?1")
    String findByFieldName(String field);


}
