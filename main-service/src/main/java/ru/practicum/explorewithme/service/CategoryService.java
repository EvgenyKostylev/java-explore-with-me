package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryDto;
import ru.practicum.explorewithme.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(int catId);

    CategoryDto updateCategory(int catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(int catId);

    Category getCategoryById(int catId);
}