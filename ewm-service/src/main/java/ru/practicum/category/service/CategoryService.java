package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto dto);

    CategoryDto updateCategory(CategoryDto dto, Long categoryId);

    void deleteCategory(Long categoryId);

    List<CategoryDto> findCategories(Integer from, Integer size);

    CategoryDto findCategoryById(Long id);

}
