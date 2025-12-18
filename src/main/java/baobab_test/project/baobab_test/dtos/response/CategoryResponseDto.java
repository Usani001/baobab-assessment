package baobab_test.project.baobab_test.dtos.response;

import baobab_test.project.baobab_test.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String path;

    public static CategoryResponseDto fromEntity(Category category) {
        CategoryResponseDto response = new CategoryResponseDto();
        response.id = category.getId();
        response.name = category.getName();
        response.description = category.getDescription();
        response.parentId = category.getParentId();
        response.path = category.getPath();
        return response;
    }

}
