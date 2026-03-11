package ru.practicum.explorewithme.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryDto;
import ru.practicum.explorewithme.expection.ConflictException;
import ru.practicum.explorewithme.expection.NotFoundException;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(mapper.toCategory(newCategoryDto));

        log.info("Save category {}", category);

        return mapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(int catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("The category is not empty");
        }

        log.info("Delete category with id={}", catId);

        categoryRepository.delete(getCategoryById(catId));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(int catId, CategoryDto categoryDto) {
        Category category = getCategoryById(catId);

        category.setName(categoryDto.getName());

        log.info("Update category {}", category);

        return mapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        log.info("Get categories: {}", categories.size());

        return mapper.toCategoriesDto(categories);
    }

    @Override
    public CategoryDto getCategory(int catId) {
        Category category = getCategoryById(catId);

        log.info("Get category {}", category);

        return mapper.toCategoryDto(category);
    }

    @Override
    public Category getCategoryById(int catId) {
        Optional<Category> categoryOptional = categoryRepository.findById(catId);

        if (categoryOptional.isEmpty()) {
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        } else {
            log.info("Find category with id={}", catId);

            return categoryOptional.get();
        }
    }
}