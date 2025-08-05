package programmerzamannow.springdata.jpa.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import programmerzamannow.springdata.jpa.entity.Category;
import org.springframework.transaction.annotation.Transactional;
import programmerzamannow.springdata.jpa.entity.Product;
import programmerzamannow.springdata.jpa.model.SimpleProduct;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findAllByCategory_Name(String name);

    List<Product> findAllByCategory_Name(String name, Sort sort);

    Page<Product> findAllByCategory_Name(String name, Pageable pageable);

    Long countByCategory_name(String name);

    boolean existsByName(String name);

    @Transactional
    int deleteByName(String name);

    List<Product> searchProductUsingName(@Param("name")  String name, Pageable pageable);

    @Query(
            value = "SELECT p FROM Product p WHERE p.name LIKE :name or p.category.name LIKE :name",
            countQuery = "SELECT COUNT(p) FROM Product p WHERE p.name LIKE :name or p.category.name LIKE :name"
    )
    Page<Product> searchProduct(@Param("name") String name, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM Product p WHERE p.name = :name")
    int deleteProductUsingName(@Param("name") String name);

    @Modifying
    @Query(value = "UPDATE Product p SET p.price = 0 WHERE p.id = :id")
    int updateProductUsingId(@Param("id") Long id);

    Stream<Product> streamAllByCategory(Category category);

    Slice<Product> findAllByCategory(Category category, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findFirstByIdEquals(Long id);

    <T>List<T> findAllByNameLike(String name, Class<T> tClass);
}
