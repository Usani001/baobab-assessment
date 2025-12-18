package baobab_test.project.baobab_test.services;

import baobab_test.project.baobab_test.dtos.request.CategoryRequestDto;
import baobab_test.project.baobab_test.dtos.response.CategoryResponseDto;
import baobab_test.project.baobab_test.entities.Category;
import baobab_test.project.baobab_test.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


//@NoArgsConstructor
public interface CategoryService {

    CategoryResponseDto addCategory(CategoryRequestDto request);

    CategoryResponseDto getSubtree(Long parentId);

    public void deleteCategory(Long id);

    public void moveCategory(Long categoryId, CategoryRequestDto request);
}
