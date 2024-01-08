package br.com.desafio.UserFieldVisibility.repository;

import br.com.desafio.UserFieldVisibility.entity.UserFieldVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFieldVisibilityRepository extends JpaRepository<UserFieldVisibility, Long> {


    @Query(nativeQuery = true, value = "UPDATE user_visibility_tbl SET is_visible_for_estoquista = ?2 where field_name = ?1")
    void changeVisibilityFieldForUser(String field, boolean isVisible);

    @Query(nativeQuery = true, value = "SELECT field_name FROM user_visibility_tbl WHERE field_name =?1")
    String findByFieldName(String field);


}
