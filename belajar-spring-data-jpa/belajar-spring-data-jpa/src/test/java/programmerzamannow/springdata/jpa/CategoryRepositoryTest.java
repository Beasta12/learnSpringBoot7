package programmerzamannow.springdata.jpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yaml.snakeyaml.emitter.ScalarAnalysis;
import programmerzamannow.springdata.jpa.entity.Category;
import programmerzamannow.springdata.jpa.repository.CategoryRepository;

import java.util.List;

@SpringBootTest
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

//    @Test
//    void insert(){
//        Category category = new Category();
//        category.setName("GADGET");
//        categoryRepository.save(category);
//
//        Assertions.assertNotNull(category.getId());
//    }

    @Test
    void update(){
        Category category = categoryRepository.findById(1L).orElse(null);

        Assertions.assertNotNull(category);

        category.setName("GADGET MURAH");
        categoryRepository.save(category);

        category = categoryRepository.findById(1L).orElse(null);
        Assertions.assertNotNull(category);
        Assertions.assertEquals("GADGET MURAH", category.getName());
    }

    @Test
    void queryMethod() {
        Category category = categoryRepository.findFirstByNameEquals("GADGET MURAH").orElse(null);
        Assertions.assertNotNull(category);
        Assertions.assertEquals("GADGET MURAH", category.getName());

        List<Category> categories = categoryRepository.findAllByNameLike("GADGET%");
        Assertions.assertEquals(1, categories.size());
        Assertions.assertEquals("GADGET MURAH", categories.get(0).getName());
    }
}
