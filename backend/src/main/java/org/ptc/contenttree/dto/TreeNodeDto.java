package org.ptc.contenttree.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeDto {
    Long id;
    String name;
    String content;
    Long parentId;
    List<TreeNodeDto> children = new ArrayList<>();
}
