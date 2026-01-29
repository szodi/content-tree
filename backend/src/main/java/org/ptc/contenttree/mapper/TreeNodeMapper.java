package org.ptc.contenttree.mapper;

import org.ptc.contenttree.dto.TreeNodeDto;
import org.ptc.contenttree.model.TreeNode;
import org.springframework.stereotype.Component;

@Component
public class TreeNodeMapper {

    public TreeNodeDto convert(TreeNode treeNode) {
        if (treeNode == null) {
            return null;
        }
        return TreeNodeDto.builder()
                .id(treeNode.getId())
                .name(treeNode.getName())
                .content(treeNode.getContent())
                .children(treeNode.getChildren() != null ? treeNode.getChildren().stream().map(this::convert).toList() : null)
                .build();
    }
}
