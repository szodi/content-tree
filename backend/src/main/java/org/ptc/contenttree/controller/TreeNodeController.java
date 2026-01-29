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
    public TreeNode create(@RequestBody Map<String,Object> body) throws IOException {
        return service.createOrUpdate(
                (String) body.get("name"),
                (String) body.get("content"),
                body.get("parentId") == null ? null : Long.valueOf(body.get("parentId").toString()),
                body.get("id") == null ? null : Long.valueOf(body.get("id").toString())
        );
    }

    @DeleteMapping("/node/{id}")
    public void delete(@PathVariable Long id) throws IOException {
        service.delete(id);
    }

    @GetMapping("/node/list")
    public TreeNodeDto listTree() {
        return service.listTree();
    }

    @GetMapping
    public TreeNode getRootNode() {
        return service.getRootNode();
    }

    @PutMapping("/move")
    public void move(@RequestBody Map<String,Long> body) throws IOException {
        service.move(body.get("nodeId"), body.get("newParentId"));
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