package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findCategories(@RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.findCategories(from, size);
    }

    @GetMapping("/{categoryId}")
    public CategoryDto findCategoryById(@PathVariable Long categoryId) {
        return categoryService.findCategoryById(categoryId);
    }

}
