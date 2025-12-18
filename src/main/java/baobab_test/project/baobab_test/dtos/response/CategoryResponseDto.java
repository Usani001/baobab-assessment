package baobab_test.project.baobab_test.dtos.response;

import baobab_test.project.baobab_test.entities.Category;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data // Replaces @Getter, @Setter, @ToString, @EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {

    private Long id;
    private String name;
    private String description;
    private Long parentId;

    private String path;

    @Builder.Default
    private List<CategoryResponseDto> children = new ArrayList<>();

    public static CategoryResponseDto fromEntity(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .path(category.getPath())
                .children(new ArrayList<>())
                .build();
    }
}