package baobab_test.project.baobab_test.controllers;

import baobab_test.project.baobab_test.dtos.request.CategoryRequestDto;
import baobab_test.project.baobab_test.dtos.response.CategoryResponseDto;
import baobab_test.project.baobab_test.services.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    public CategoryResponseDto add(
            @RequestBody @Valid CategoryRequestDto request
    ) {
        return service.addCategory(request);
    }

    @GetMapping("/subtree/{id}")
    public List<CategoryResponseDto> subtree(@PathVariable Long id) {
        return service.getSubtree(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteCategory(id);
    }

    @PostMapping("/move/{id}")
    public void move(
            @PathVariable Long id,
            @RequestParam CategoryRequestDto request
    ) {
        service.moveCategory(id, request);
    }
}
