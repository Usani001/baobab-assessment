package baobab_test.project.baobab_test.services.service_impl;

import baobab_test.project.baobab_test.dtos.request.CategoryRequestDto;
import baobab_test.project.baobab_test.dtos.response.CategoryResponseDto;
import baobab_test.project.baobab_test.entities.Category;
import baobab_test.project.baobab_test.repository.CategoryRepository;
import baobab_test.project.baobab_test.services.CategoryService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto addCategory(CategoryRequestDto request) {
        try {
            String parentPath = "/";

            if (request.getParentId() != null) {
                Category parent = categoryRepository.findById(request.getParentId() )
                        .orElseThrow(() -> new NoSuchElementException("Parent not found"));
                parentPath = parent.getPath();
            }

            Category category = new Category();
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            category.setParentId(request.getParentId());
            category.setPath("TEMP");

            category = categoryRepository.save(category);

            String path = parentPath + category.getId() + "/";
            category.setPath(path);

            return CategoryResponseDto.fromEntity(categoryRepository.save(category));

        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add category", e);
        }
    }

    public List<CategoryResponseDto> getSubtree(Long parentId) {
        try {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));
            return categoryRepository
                    .findAllByPathStartingWith(parent.getPath())
                    .stream()
                    .map(CategoryResponseDto::fromEntity)
                    .toList();

        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch subtree", e);
        }
    }

    @Transactional
    public void deleteCategory(Long id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));
            categoryRepository.deleteByPathStartingWith(category.getPath());

        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete category", e);
        }
    }

    @Transactional
    public void moveCategory(Long categoryId, CategoryRequestDto request) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));

            Category newParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NoSuchElementException("New parent not found"));

            if (newParent.getPath().startsWith(category.getPath())) {
                throw new IllegalArgumentException("Cannot move category inside its own subtree");
            }

            String oldPath = category.getPath();
            String newPath = newParent.getPath() + category.getId() + "/";

            categoryRepository.updateSubtreePath(oldPath, newPath, oldPath.length());

            category.setParentId(request.getParentId());
            category.setPath(newPath);
            categoryRepository.save(category);

        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to move category", e);
        }
    }
}

