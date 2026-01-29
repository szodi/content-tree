package org.ptc.contenttree.mapper;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.dto.TreeNodeDto;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.service.JsonTreeStorage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TreeNodeMapper {

    private final JsonTreeStorage storage;

    public TreeNodeDto convert(TreeNode treeNode) {
        if (treeNode == null) {
            return null;
        }
        return TreeNodeDto.builder()
                .id(treeNode.getId())
                .name(treeNode.getName())
                .content(treeNode.getContent())
                .parentId(treeNode.getParentId())
                .children(treeNode.getChildrenIds() != null ? buildChildren(treeNode.getChildrenIds()).stream().map(this::convert).toList() : null)
                .build();
    }

    private List<TreeNode> buildChildren(List<Long> childrenIds) {
        return childrenIds.stream().map(storage::findById).toList();
    }
}
