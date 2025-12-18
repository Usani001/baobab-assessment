package baobab_test.project.baobab_test.dtos.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDto {

    @NotBlank
    @Schema(description = "Category name", example = "Music")
    private String name;

    @NotBlank
    @Schema(description = "Category description", example = "All music events")
    private String description;

    @NotNull
    @Schema(description = "Parent category ID, null for root", example = "1")
    private Long parentId;

    @NotNull
    @Schema(description = "New Parent ID", example = "1")
    private Long newParentId;
}
