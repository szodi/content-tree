package org.ptc.contenttree.service;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.repository.TreeNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class TreeNodeService {

    private final TreeNodeRepository repository;

    public TreeNode createOrUpdate(String name, String content, Long parentId, Long id) {
        validateParentForUpsert(id, parentId);

        TreeNode node = id == null
                ? TreeNode.builder().build()
                : repository.findById(id).orElseThrow(() -> new NoSuchElementException("Node not found: " + id));

        Long oldParentId = node.getParentId();

        node.setName(name);
        node.setContent(content);
        node.setParentId(parentId);
        TreeNode saved = repository.save(node);

        if (!Objects.equals(oldParentId, parentId)) {
            removeChildFromParent(oldParentId, saved.getId());
            addChildToParent(parentId, saved.getId());
        } else if (parentId != null) {
            addChildToParent(parentId, saved.getId());
        }

        return saved;
    }

    public void delete(Long id) {
        TreeNode node = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Node not found: " + id));

        removeChildFromParent(node.getParentId(), id);
        for (Long childId : List.copyOf(node.getChildrenIds())) {
            delete(childId);
        }

        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Collection<TreeNode> getAllNodes() {
        return repository.findAll();
    }

    public Collection<TreeNode> move(Long nodeId, Long newParentId) {
        if (Objects.equals(nodeId, newParentId)) {
            throw new IllegalArgumentException("Node cannot be parent of itself.");
        }

        TreeNode node = repository.findById(nodeId).orElseThrow(() -> new NoSuchElementException("Node not found: " + nodeId));
        repository.findById(newParentId).orElseThrow(() -> new NoSuchElementException("Parent node not found: " + newParentId));
        ensureNotDescendant(nodeId, newParentId);

        removeChildFromParent(node.getParentId(), nodeId);
        addChildToParent(newParentId, nodeId);

        node.setParentId(newParentId);
        repository.save(node);

        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public String loadContent(Long id) {
        return repository.findById(id)
                .map(TreeNode::getContent)
                .orElseThrow(() -> new NoSuchElementException("Node not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<TreeNode> search(String text) {
        String searchText = text == null ? "" : text.toLowerCase();

        return repository.findAll().stream()
                .filter(n -> n.getName().toLowerCase().contains(searchText)
                        || n.getContent().toLowerCase().contains(searchText))
                .toList();
    }

    private void validateParentForUpsert(Long id, Long parentId) {
        if (parentId == null) {
            return;
        }
        if (Objects.equals(id, parentId)) {
            throw new IllegalArgumentException("Node cannot be parent of itself.");
        }
        repository.findById(parentId).orElseThrow(() -> new NoSuchElementException("Parent node not found: " + parentId));
        if (id != null) {
            ensureNotDescendant(id, parentId);
        }
    }

    private void ensureNotDescendant(Long nodeId, Long candidateParentId) {
        Long currentId = candidateParentId;
        while (currentId != null) {
            if (Objects.equals(currentId, nodeId)) {
                throw new IllegalArgumentException("Cannot move node under its own descendant.");
            }
            currentId = repository.findById(currentId).map(TreeNode::getParentId).orElse(null);
        }
    }

    private void removeChildFromParent(Long parentId, Long childId) {
        if (parentId == null) {
            return;
        }
        repository.findById(parentId).ifPresent(parent -> {
            if (parent.getChildrenIds().remove(childId)) {
                repository.save(parent);
            }
        });
    }

    private void addChildToParent(Long parentId, Long childId) {
        if (parentId == null) {
            return;
        }
        TreeNode parent = repository.findById(parentId)
                .orElseThrow(() -> new NoSuchElementException("Parent node not found: " + parentId));

        if (!parent.getChildrenIds().contains(childId)) {
            parent.getChildrenIds().add(childId);
            repository.save(parent);
        }
    }
}
