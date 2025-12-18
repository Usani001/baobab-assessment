package baobab_test.project.baobab_test.services.service_impl;

import baobab_test.project.baobab_test.dtos.request.CategoryRequestDto;
import baobab_test.project.baobab_test.dtos.response.CategoryResponseDto;
import baobab_test.project.baobab_test.entities.Category;
import baobab_test.project.baobab_test.repository.CategoryRepository;
import baobab_test.project.baobab_test.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Cleaner than AllArgsConstructor for final fields
@Transactional // Class-level transaction management reduces repetition
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto addCategory(CategoryRequestDto request) {
        // 1. Determine Parent Path
        String parentPath = "/";
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NoSuchElementException("Parent category not found"));
            parentPath = parent.getPath();
        }

        // 2. Create & Save (First Save to generate ID)
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());

        // Optimization: If using MySQL (IDENTITY), this double-save is unavoidable.
        // If using Postgres (SEQUENCE), we could set ID manually before saving to avoid this.
        category.setPath("TEMP");
        category = categoryRepository.save(category);

        // 3. Update Path & Final Save
        category.setPath(parentPath + category.getId() + "/");
        return CategoryResponseDto.fromEntity(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true) // Read-only is more efficient for fetch operations
    public CategoryResponseDto getSubtree(Long parentId) {
        // 1. Fetch Root Node
        Category rootNode = categoryRepository.findById(parentId)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));

        // 2. Fetch all descendants in one SQL query (Materialized Path efficiency)
        List<Category> flatList = categoryRepository.findAllByPathStartingWith(rootNode.getPath());

        // 3. Efficiently build Tree in-memory (O(n) complexity)
        return buildTreeHierarchy(flatList, rootNode);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));

        // Efficient: Deletes parent and all children in one transaction based on path
        categoryRepository.deleteByPathStartingWith(category.getPath());
    }

    @Override
    public void moveCategory(Long categoryId, CategoryRequestDto request) {
        Category nodeToMove = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));

        Category newParent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new NoSuchElementException("New parent not found"));

        // 1. Circular Dependency Check
        if (newParent.getPath().startsWith(nodeToMove.getPath())) {
            throw new IllegalArgumentException("Cannot move category inside its own subtree");
        }

        // 2. Calculate Paths
        String oldPath = nodeToMove.getPath();
        String newPath = newParent.getPath() + nodeToMove.getId() + "/";

        // 3. Bulk Update Descendants (SQL update is faster than iterating objects)
        // Ensure your Repo query handles string replacement correctly
        categoryRepository.updateSubtreePath(oldPath, newPath, oldPath.length());

        // 4. Update the Moved Node
        nodeToMove.setParentId(newParent.getId());
        nodeToMove.setPath(newPath);
        categoryRepository.save(nodeToMove);
    }

    // --- Helper for Tree Building (O(n)) ---
    private CategoryResponseDto buildTreeHierarchy(List<Category> flatList, Category rootNode) {
        // Map ID -> DTO
        Map<Long, CategoryResponseDto> dtoMap = new HashMap<>();
        CategoryResponseDto rootDto = null;

        // Convert all to DTOs and put in Map
        for (Category cat : flatList) {
            CategoryResponseDto dto = CategoryResponseDto.fromEntity(cat);
            dtoMap.put(cat.getId(), dto);
            if (cat.getId().equals(rootNode.getId())) {
                rootDto = dto;
            }
        }

        // Stitch the tree together
        for (Category cat : flatList) {
            CategoryResponseDto dto = dtoMap.get(cat.getId());
            Long parentId = cat.getParentId();

            if (parentId != null && dtoMap.containsKey(parentId)) {
                // Add this node to its parent's children list
                dtoMap.get(parentId).getChildren().add(dto);
            }
        }

        return rootDto;
    }
}