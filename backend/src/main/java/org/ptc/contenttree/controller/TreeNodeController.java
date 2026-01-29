package org.ptc.contenttree.controller;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.dto.TreeNodeDto;
import org.ptc.contenttree.service.TreeNodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/nodes")
@RequiredArgsConstructor
public class TreeNodeController {

    private final TreeNodeService treeNodeService;

    @GetMapping("/")
    public ResponseEntity<TreeNodeDto> getRootTreeNode() {
        return ResponseEntity.of(Optional.ofNullable(treeNodeService.getRootTreeNode()));
    }

    @PostMapping("/{parentNodeId}")
    public ResponseEntity<TreeNodeDto> createTreeNode(@PathVariable Integer parentNodeId, @RequestBody TreeNodeDto treeNodeDto) {
        return ResponseEntity.of(Optional.ofNullable(treeNodeService.createTreeNode(parentNodeId, treeNodeDto)));
    }

    @PutMapping("/")
    public ResponseEntity<TreeNodeDto> updateTreeNode(@RequestBody TreeNodeDto treeNodeDto) {
        return ResponseEntity.of(Optional.ofNullable(treeNodeService.updateTreeNode(treeNodeDto)));
    }

    @DeleteMapping("/{nodeId}")
    public void deleteTreeNode(@PathVariable Integer nodeId) {
        treeNodeService.deleteTreeNode(nodeId);
    }

    @GetMapping("/{nodeId}")
    public ResponseEntity<TreeNodeDto> getTreeNode(@PathVariable Integer nodeId) {
        return ResponseEntity.of(Optional.ofNullable(treeNodeService.getTreeNodeWithChildren(nodeId)));
    }

    @PutMapping("/{nodeId}/{parentNodeId}")
    public ResponseEntity<TreeNodeDto> relocateTreeNode(@PathVariable Integer nodeId, @PathVariable Integer parentNodeId) {
        return ResponseEntity.of(Optional.ofNullable(treeNodeService.relocateTreeNode(nodeId, parentNodeId)));
    }
}
