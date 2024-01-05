package br.com.desafio.service;

import br.com.desafio.entity.Category;
import br.com.desafio.entity.enums.CategoryType;
import br.com.desafio.exception.Category.CategoryNotFoundException;
import br.com.desafio.exception.User.UserNotFoundException;
import br.com.desafio.repository.CategoryRepository;
import br.com.desafio.util.CategoryCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    Category category;

    @BeforeEach
    public void setUp() {
        category = CategoryCreator.createCategory();
    }

    @Test
    void shouldFindCategory() throws CategoryNotFoundException {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category category = categoryService.getCategory(1L);

        assertNotNull(category);
    }

    @Test
    void shouldNotFindCategoryBecauseNotExist() {
        final CategoryNotFoundException e = assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getCategory(2L);
        });

        assertThat(e, notNullValue());
    }
}
