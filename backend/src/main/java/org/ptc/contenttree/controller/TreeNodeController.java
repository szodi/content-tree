package org.ptc.contenttree.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.dto.TreeNodeUpsertRequest;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.service.TreeNodeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/tree")
@RequiredArgsConstructor
public class TreeNodeController {

    private final TreeNodeService service;

    @PostMapping("/node")
    public TreeNode create(@Valid @RequestBody TreeNodeUpsertRequest request) {
        return service.createOrUpdate(
                request.name(),
                request.content(),
                request.parentId(),
                request.id()
        );
    }

    @DeleteMapping("/node/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNode(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/node/all")
    public Collection<TreeNode> getAllNodes() {
        return service.getAllNodes();
    }

    @PutMapping("/move/{nodeId}/{newParentNodeId}")
    public Collection<TreeNode> move(@PathVariable Long nodeId, @PathVariable Long newParentNodeId) {
        return service.move(nodeId, newParentNodeId);
    }

    @GetMapping("/content/{id}")
    public Map<String, String> load(@PathVariable Long id) {
        return Map.of("content", Optional.ofNullable(service.loadContent(id)).orElse(""));
    }

    @GetMapping("/search")
    public List<TreeNode> search(@RequestParam String text) {
        return service.search(text);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NoSuchElementException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }
}
