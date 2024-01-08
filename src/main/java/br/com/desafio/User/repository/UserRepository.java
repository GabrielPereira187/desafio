package br.com.desafio.User.repository;

import br.com.desafio.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = "SELECT user_name FROM tbl_user WHERE user_id = ?1")
    String findUsernameById(Long userId);
    UserDetails findByEmail(String email);
    UserDetails findByEmailAndPassword(String email, String password);
}
