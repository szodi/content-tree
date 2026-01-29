package org.ptc.contenttree.controller;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.dto.TreeNodeDto;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.service.TreeNodeService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tree")
@RequiredArgsConstructor
public class TreeNodeController {

    private final TreeNodeService service;

    @PostMapping("/node")
    public TreeNode create(@RequestBody TreeNodeDto treeNodeDto) throws IOException {
        return service.createOrUpdate(
                treeNodeDto.getName(),
                treeNodeDto.getContent(),
                treeNodeDto.getParentId(),
                treeNodeDto.getId()
        );
    }

    @DeleteMapping("/node/{id}")
    public void deleteNode(@PathVariable Long id) throws IOException {
        service.delete(id);
    }

    @GetMapping("/node/list")
    public TreeNodeDto listTree() {
        return service.listTree();
    }

    @PutMapping("/move/{nodeId}/{newParentNodeId}")
    public TreeNodeDto move(@PathVariable Long nodeId, @PathVariable Long newParentNodeId) throws IOException {
        return service.move(nodeId, newParentNodeId);
    }

    @GetMapping("/content/{id}")
    public Map<String,String> load(@PathVariable Long id) {
        return Map.of("content", service.loadContent(id));
    }

    @GetMapping("/search")
    public List<TreeNode> search(@RequestParam String text) {
        return service.search(text);
    }
}