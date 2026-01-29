package org.ptc.contenttree.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeNode {
    private Long id;
    private String name;
    private String content;
    private Long parentId; // reference only
    private List<Long> childrenIds = new ArrayList<>();
}