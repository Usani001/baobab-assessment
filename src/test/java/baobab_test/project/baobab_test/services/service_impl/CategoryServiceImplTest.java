package baobab_test.project.baobab_test.services.service_impl;

import baobab_test.project.baobab_test.dtos.request.CategoryRequestDto;
import baobab_test.project.baobab_test.entities.Category;
import baobab_test.project.baobab_test.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    // ---------------- ADD CATEGORY ----------------

    @Test
    void addCategory_root_success() {
        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Music");
        request.setDescription("Music events");
        request.setParentId(null);

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category c = invocation.getArgument(0);
                    if (c.getId() == null) {
                        c.setId(1L); // simulate DB-generated ID
                    }
                    return c;
                });

        var response = categoryService.addCategory(request);

        assertEquals("Music", response.getName());
        assertEquals("Music events", response.getDescription());
        assertEquals("/1/", response.getPath());

        verify(categoryRepository, times(2)).save(any());
    }


    @Test
    void addCategory_parentNotFound_throwsException() {
        CategoryRequestDto request = new CategoryRequestDto();
        request.setParentId(99L);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> categoryService.addCategory(request));
    }

    // ---------------- GET SUBTREE ----------------

    @Test
    void getSubtree_success() {
        Category parent = new Category();
        parent.setId(1L);
        parent.setPath("/1/");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(categoryRepository.findAllByPathStartingWith("/1/"))
                .thenReturn(List.of(parent));

        var result = categoryService.getSubtree(1L);

        assertEquals(1, result.size());
    }

    // ---------------- DELETE CATEGORY ----------------

    @Test
    void deleteCategory_success() {
        Category category = new Category();
        category.setId(1L);
        category.setPath("/1/");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteByPathStartingWith("/1/");
    }

    // ---------------- MOVE CATEGORY ----------------

    @Test
    void moveCategory_success() {
        Category category = new Category();
        category.setId(2L);
        category.setPath("/1/2/");

        Category newParent = new Category();
        newParent.setId(3L);
        newParent.setPath("/3/");

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(newParent));

        CategoryRequestDto request = new CategoryRequestDto();
        request.setParentId(3L);

        categoryService.moveCategory(2L, request);

        verify(categoryRepository).updateSubtreePath(any(), any(), anyInt());
    }

    @Test
    void moveCategory_intoOwnSubtree_throwsException() {
        Category category = new Category();
        category.setId(1L);
        category.setPath("/1/");

        Category child = new Category();
        child.setId(2L);
        child.setPath("/1/2/");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(child));

        CategoryRequestDto request = new CategoryRequestDto();
        request.setParentId(2L);

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.moveCategory(1L, request));
    }
}
