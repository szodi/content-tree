package org.ptc.contenttree.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeDto {
    Integer id;
    String name;
    String content;
    List<TreeNodeDto> children;
    Integer parentId;
}
