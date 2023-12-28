package br.com.desafio.service;


import br.com.desafio.entity.Category;
import br.com.desafio.exception.Category.CategoryNotFoundException;
import br.com.desafio.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category getCategory(Long categoryId) throws CategoryNotFoundException {

        log.info("Buscando categoria com id:{}", categoryId);

        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
}
