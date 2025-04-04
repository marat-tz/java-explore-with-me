package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.reposiory.CategoryRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(CategoryDto dto) {
        Category category = categoryRepository.save(categoryMapper.toEntity(dto));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto dto, Long categoryId) {
        checkExist(categoryId);
        dto.setId(categoryId);
        Category category = categoryRepository.save(categoryMapper.toEntity(dto));
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> findCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        return categories.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto findCategoryById(Long categoryId) {
        checkExist(categoryId);
        Optional<Category> category = categoryRepository.findById(categoryId);
        return categoryMapper.toDto(category.get());
    }

    private void checkExist(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Категория с id = " + categoryId + " не найдена.");
        }
    }


}
