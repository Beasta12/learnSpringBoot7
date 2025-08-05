package programmerzamannow.springdata.jpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionOperations;
import programmerzamannow.springdata.jpa.entity.Category;
import programmerzamannow.springdata.jpa.entity.Product;
import programmerzamannow.springdata.jpa.model.ProductPrice;
import programmerzamannow.springdata.jpa.model.SimpleProduct;
import programmerzamannow.springdata.jpa.repository.CategoryRepository;
import programmerzamannow.springdata.jpa.repository.ProductRepository;

import java.util.List;
import java.util.stream.Stream;

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
    void testDelete() {
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

            delete = productRepository.deleteByName("Samsung Galaxy S9");
            Assertions.assertEquals(0, delete);
        });
    }

    @Test
    void stream() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);
            Assertions.assertNotNull(category);

            Stream<Product> stream = productRepository.streamAllByCategory(category);
            stream.forEach((product -> System.out.println(product.getId() + " : " + product.getName())));
        });
    }

    @Test
    void slice() {
        Pageable pageable = PageRequest.of(0, 1);
        Category category = categoryRepository.findById(1L).orElse(null);
        Assertions.assertNotNull(category);

        Slice<Product> slice = productRepository.findAllByCategory(category, pageable);
        while (slice.hasNext()) {
            slice = productRepository.findAllByCategory(category, slice.nextPageable());
        }
    }

    @Test
    void lock1() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            try {
                Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
                Assertions.assertNotNull(product);

                product.setPrice(30_000_000L);
                Thread.sleep(20_000L);
                productRepository.save(product);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Test
    void lock2() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
            Assertions.assertNotNull(product);
            product.setPrice(10_000_000L);
            productRepository.save(product);
        });
    }

    @Test
    void audit() {
        Category category = new Category();
        category.setName("Sample Audit");
        categoryRepository.save(category);

        Assertions.assertNotNull(category.getId());
        Assertions.assertNotNull(category.getCreatedDate());
        Assertions.assertNotNull(category.getLastModifiedDate());
    }

    @Test
    void example() {
        Category category = new Category();
        category.setName("GADGET MURAH");

        Example<Category> example = Example.of(category);

        List<Category> categories = categoryRepository.findAll(example);
        Assertions.assertEquals(1, categories.size());
    }

    @Test
    void example2() {
        Category category = new Category();
        category.setName("GADGET MURAH");
        category.setId(1L);

        Example<Category> example = Example.of(category);

        List<Category> categories = categoryRepository.findAll(example);
        Assertions.assertEquals(1, categories.size());
    }

    @Test
    void exampleMatcher() {
        Category category = new Category();
        category.setName("gadget MURAH");

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnoreCase();

        Example<Category> example = Example.of(category, matcher);

        List<Category> categories = categoryRepository.findAll(example);
        Assertions.assertEquals(1, categories.size());
    }

    @Test
    void specification() {
        Specification<Product> specification = (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaQuery.where(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("name"), "Apple iPhone 14 Pro Max"),
                            criteriaBuilder.equal(root.get("name"), "Apple iPhone 13 Pro Max")
                    )
            ).getRestriction();
        };

        List<Product> products = productRepository.findAll(specification);
        Assertions.assertEquals(2, products.size());
    }

    @Test
    void projection() {
        List<SimpleProduct> simpleProducts = productRepository.findAllByNameLike("%Apple%", SimpleProduct.class);
        Assertions.assertEquals(2, simpleProducts.size());

        List<ProductPrice> productPrices = productRepository.findAllByNameLike("%Apple%", ProductPrice.class);
        Assertions.assertEquals(2, productPrices.size());
    }
}
