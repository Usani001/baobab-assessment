package baobab_test.project.baobab_test.controllers;

import baobab_test.project.baobab_test.dtos.request.CategoryRequestDto;
import baobab_test.project.baobab_test.dtos.response.CategoryResponseDto;
import baobab_test.project.baobab_test.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "Category API", description = "Operations for managing event categories")
public class CategoryController {

    private final CategoryService service;

    @PostMapping("/add-category")
    @Operation(summary = "Add a new category", description = "Creates a new category under the specified parent")
    public CategoryResponseDto add(
            @RequestBody @Valid CategoryRequestDto request
    ) {
        return service.addCategory(request);
    }

    @GetMapping("/subtree/{id}")
    @Operation(summary = "Get category subtree", description = "Fetches all categories under the given parent category ID")
    public List<CategoryResponseDto> subtree(@PathVariable Long id) {
        return service.getSubtree(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a category and all its subcategories by ID")
    public void delete(@PathVariable Long id) {
        service.deleteCategory(id);
    }

    @PostMapping("/move/{id}")
    @Operation(summary = "Move a category", description = "Moves a category to a new parent")
    public void move(
            @PathVariable Long id,
            @RequestParam CategoryRequestDto request
    ) {
        service.moveCategory(id, request);
    }
}
