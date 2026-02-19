package org.ptc.contenttree.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TreeNodeUpsertRequest(
        Long id,
        @NotBlank @Size(max = 255) String name,
        @NotBlank String content,
        Long parentId
) {
}
