package org.ptc.contenttree.controller;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.service.TreeNodeService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tree")
@RequiredArgsConstructor
public class TreeNodeController {

    private final TreeNodeService service;

    @PostMapping("/node")
    public TreeNode create(@RequestBody Map<String,Object> body) throws IOException {
        return service.createOrUpdate(
                (String) body.get("name"),
                (String) body.get("content"),
                body.get("parentId") == null ? null : Long.valueOf(body.get("parentId").toString()),
                body.get("id") == null ? null : Long.valueOf(body.get("id").toString())
        );
    }

    @DeleteMapping("/node/{id}")
    public void deleteNode(@PathVariable Long id) throws IOException {
        service.delete(id);
    }

    @GetMapping("/node/all")
    public Collection<TreeNode> getAllNodes() {
        return service.getAllNodes();
    }

    @PutMapping("/move/{nodeId}/{newParentNodeId}")
    public Collection<TreeNode> move(@PathVariable Long nodeId, @PathVariable Long newParentNodeId) throws IOException {
        return service.move(nodeId, newParentNodeId);
    }

    @GetMapping("/content/{id}")
    public Map<String,String> load(@PathVariable Long id) {
        return Map.of("content", Optional.ofNullable(service.loadContent(id)).orElse(""));
    }

    @GetMapping("/search")
    public List<TreeNode> search(@RequestParam String text) {
        return service.search(text);
    }
}