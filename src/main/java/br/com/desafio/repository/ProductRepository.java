package br.com.desafio.repository;

import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, RevisionRepository<Product, Long, Long> , JpaSpecificationExecutor {

    @Query(
            nativeQuery = true,
            value = "UPDATE product_table SET product_active = b'0' where product_id = ?1"
    )
    void deactivateProduct(Long id);

    Page<Product> findByUserId(Long userId, Pageable pageRequest);
}
