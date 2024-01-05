package br.com.desafio.util;

import br.com.desafio.entity.Category;
import br.com.desafio.entity.enums.CategoryType;

public class CategoryCreator {

    public static Category createCategory () {
        return Category.builder().categoryId(1L).categoryName("Teste").activeCategory(true).categoryType(CategoryType.NORMAL).build();
    }
}
