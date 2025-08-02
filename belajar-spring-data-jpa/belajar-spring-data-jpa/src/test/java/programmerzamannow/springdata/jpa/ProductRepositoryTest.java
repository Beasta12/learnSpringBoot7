package programmerzamannow.springdata.jpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionOperations;
import programmerzamannow.springdata.jpa.entity.Category;
import programmerzamannow.springdata.jpa.entity.Product;
import programmerzamannow.springdata.jpa.repository.CategoryRepository;
import programmerzamannow.springdata.jpa.repository.ProductRepository;

import java.util.List;

@SpringBootTest
public class ProductRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionOperations transactionOperations;

    @Test
    void createProduct() {
        Category category = categoryRepository.findById(1L).orElse(null);
        Assertions.assertNotNull(category);

        {
            Product product = new Product();
            product.setName("Apple iPhone 14 Pro Max");
            product.setPrice(25_000_000L);
            product.setCategory(category);

            productRepository.save(product);
        }
        {
            Product product = new Product();
            product.setName("Apple iPhone 13 Pro Max");
            product.setPrice(10_000_000L);
            product.setCategory(category);

            productRepository.save(product);
        }

    }

    @Test
    void findByCategoryName() {
        List<Product> products = productRepository.findAllByCategory_Name("GADGET MURAH");

        Assertions.assertEquals(2, products.size());

        Assertions.assertEquals("Apple iPhone 14 Pro Max", products.get(0).getName());
        Assertions.assertEquals("Apple iPhone 13 Pro Max", products.get(1).getName());
    }

    @Test
    void findProductSort() {
        Sort sort = Sort.by(Sort.Order.desc("id"));

        List<Product> products = productRepository.findAllByCategory_Name("GADGET MURAH", sort);
        Assertions.assertEquals(2, products.size());
        Assertions.assertEquals("Apple iPhone 13 Pro Max", products.get(0).getName());
        Assertions.assertEquals("Apple iPhone 14 Pro Max", products.get(1).getName());
    }
    @Test
    void findProductPageable() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));
        Page<Product> products = productRepository.findAllByCategory_Name("GADGET MURAH", pageable);
//        page 0
        Assertions.assertEquals(1, products.getContent().size());
        Assertions.assertEquals("Apple iPhone 13 Pro Max", products.getContent().get(0).getName());

//        page 1
        pageable = PageRequest.of(1, 1, Sort.by(Sort.Order.desc("id")));
        products = productRepository.findAllByCategory_Name("GADGET MURAH", pageable);

        Assertions.assertEquals(1, products.getContent().size());
        Assertions.assertEquals("Apple iPhone 14 Pro Max", products.getContent().get(0).getName());
    }

    @Test
    void testCount() {
        Long count = productRepository.count();
        Assertions.assertEquals(2, count);

        count = productRepository.countByCategory_name("GADGET MURAH");
        Assertions.assertEquals(2, count);
    }

    @Test
    void testExists() {
        boolean exist = productRepository.existsByName("Apple iPhone 14 Pro Max");
        Assertions.assertTrue(exist);

        exist = productRepository.existsByName("Samsung Galaxy s23");
        Assertions.assertFalse(exist);
    }

    @Test
    void testDeleteOld() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);
            Assertions.assertNotNull(category);

            Product product = new Product();
            product.setName("Samsung Galaxy S9");
            product.setPrice(10_000_000L);
            product.setCategory(category);
            productRepository.save(product);

            int delete = productRepository.deleteByName("Samsung Galaxy S9");
            Assertions.assertEquals(1, delete);

            delete  = productRepository.deleteByName("Samsung Galaxy S9");
            Assertions.assertEquals(0, delete);
        });
    }

    @Test
    void testDelete() {
//        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);
            Assertions.assertNotNull(category);

            Product product = new Product();
            product.setName("Samsung Galaxy S9");
            product.setPrice(10_000_000L);
            product.setCategory(category);
            productRepository.save(product);

            int delete = productRepository.deleteByName("Samsung Galaxy S9");
            Assertions.assertEquals(1, delete);

            delete  = productRepository.deleteByName("Samsung Galaxy S9");
            Assertions.assertEquals(0, delete);
//        });
    }

    @Test
    void searchProductUsingName() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));

        List<Product> products = productRepository.searchProductUsingName("Apple iPhone 14 Pro Max", pageable);
        Assertions.assertEquals(1, products.size());
        Assertions.assertEquals("Apple iPhone 14 Pro Max", products.get(0).getName());

    }

    @Test
    void searchProduct() {
        Pageable pageable= PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));

        Page<Product> products = productRepository.searchProduct("Apple%", pageable);
        Assertions.assertEquals(1, products.getContent().size());


        Assertions.assertEquals(0, products.getNumber());
        Assertions.assertEquals(2, products.getTotalPages());
        Assertions.assertEquals(2, products.getTotalElements());

        products = productRepository.searchProduct("GADGET MURAH", pageable);
        Assertions.assertEquals(1, products.getContent().size());

        Assertions.assertEquals(0, products.getNumber());
        Assertions.assertEquals(2, products.getTotalPages());
        Assertions.assertEquals(2, products.getTotalElements());
    }

    @Test
    void modifying() {
        transactionOperations.executeWithoutResult(TransactionStatus -> {
            int total = productRepository.deleteProductUsingName("Wrong");
            Assertions.assertEquals(0, total);

            total = productRepository.updateProductUsingId(1L);
            Assertions.assertEquals(1, total);

            Product product = productRepository.findById(1L).orElse(null);
            Assertions.assertNotNull(product);
            Assertions.assertEquals(0L, product.getPrice());
        });
    }
}
