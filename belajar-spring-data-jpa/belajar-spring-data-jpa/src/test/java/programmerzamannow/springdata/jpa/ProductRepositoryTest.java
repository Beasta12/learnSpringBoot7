package programmerzamannow.springdata.jpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}
