package org.ptc.contenttree.service;

import org.ptc.contenttree.dto.TreeNodeDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TreeNodeService {
    TreeNodeDto getRootTreeNode();
    TreeNodeDto getTreeNodeWithChildren(Integer rootNodeId);
    TreeNodeDto createTreeNode(Integer parentNodeId, TreeNodeDto treeNodeDto);
    TreeNodeDto updateTreeNode(@RequestBody TreeNodeDto treeNodeDto);
    TreeNodeDto getTreeNode(@PathVariable Integer nodeId);
    TreeNodeDto relocateTreeNode(@PathVariable Integer nodeId, @PathVariable Integer newParentNodeId);
    void deleteTreeNode(Integer nodeId);
}
