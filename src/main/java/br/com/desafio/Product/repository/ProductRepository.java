package br.com.desafio.Product.repository;

import br.com.desafio.Product.entity.Product;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, RevisionRepository<Product, Long, Long> , JpaSpecificationExecutor {

    @Query(
            nativeQuery = true,
            value = "UPDATE product_table SET product_active = b'0' where product_id = ?1"
    )
    void deactivateProduct(Long id);

    Page<Product> findByUserId(Long userId, Pageable pageRequest);

    @Query(
            nativeQuery = true,
            value = "select product_id as productId, \n" +
                    "product_name as name, \n" +
                    "product_active as activeProduct,  \n" +
                    "product_sku as SKU,\n" +
                    "category_id as categoryId,\n" +
                    "product_cost as cost,\n" +
                    "product_icms as ICMS,\n" +
                    "product_revenue_value as revenueValue,\n" +
                    "product_image as image,\n" +
                    "product_created_at as entryDate,\n" +
                    "product_updated_at as updatedDate,\n" +
                    "user_id as userId,\n" +
                    "product_quantity as quantity\n" +
                    "from product_table_aud where rev = ?1 AND revtype = 1")
    Tuple findRevisionByRevType(Long revisionId);

    @Query(
            nativeQuery = true,
            value = "select product_id as productId, \n" +
                    "product_name as name, \n" +
                    "product_active as activeProduct,  \n" +
                    "product_sku as SKU,\n" +
                    "category_id as categoryId,\n" +
                    "product_cost as cost,\n" +
                    "product_icms as ICMS,\n" +
                    "product_revenue_value as revenueValue,\n" +
                    "product_image as image,\n" +
                    "product_created_at as entryDate,\n" +
                    "product_updated_at as updatedDate,\n" +
                    "user_id as userId,\n" +
                    "product_quantity as quantity\n" +
                    "from product_table_aud where rev < ?2 AND product_id = ?1 ORDER BY rev desc LIMIT 1")
    Tuple findLastRevisionByProductIdAndRev(Long productId, Long revisionId);
}
