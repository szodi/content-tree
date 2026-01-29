package org.ptc.contenttree.service;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.dto.TreeNodeDto;
import org.ptc.contenttree.mapper.TreeNodeMapper;
import org.ptc.contenttree.model.TreeNode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TreeNodeService {

    private final JsonTreeStorage storage;
    private final TreeNodeMapper treeNodeMapper;

    public TreeNode createOrUpdate(String name, String content, Long parentId, Long id) throws IOException {
        TreeNode node = (id != null) ? storage.findById(id) : new TreeNode();
        if (node == null) node = new TreeNode();
        node.setName(name);
        node.setContent(content);
        node.setParentId(parentId);

        node = storage.createOrUpdate(node);
        if (parentId != null) {
            TreeNode parent = storage.findById(parentId);
            if (!parent.getChildrenIds().contains(node.getId())) {
                parent.getChildrenIds().add(node.getId());
                storage.createOrUpdate(parent);
            }
        }

        return node;
    }

    public void delete(Long id) throws IOException {
        storage.delete(id);
        storage.save();
    }

    public TreeNodeDto listTree() {
        return storage.findAll().stream()
                .filter(n -> n.getParentId() == null)
                .map(treeNodeMapper::convert)
                .findFirst()
                .orElse(null);
    }

    public TreeNode getRootNode() {
        return storage.findAll().stream()
                .filter(n -> n.getParentId() == null)
                .findFirst()
                .orElse(null);
    }

    public void move(Long nodeId, Long newParentId) throws IOException {
        TreeNode node = storage.findById(nodeId);
        TreeNode newParent = storage.findById(newParentId);

        if (node.getParentId() != null) {
            TreeNode oldParent = storage.findById(node.getParentId());
            oldParent.getChildrenIds().remove(nodeId);
            storage.createOrUpdate(oldParent);
        }

        node.setParentId(newParentId);
        newParent.getChildrenIds().add(nodeId);

        storage.createOrUpdate(newParent);
        storage.createOrUpdate(node);
    }

    public String loadContent(Long id) {
        return storage.findById(id).getContent();
    }

    public List<TreeNode> search(String text) {
        return storage.findAll().stream()
                .filter(n -> n.getName().toLowerCase().contains(text.toLowerCase()) ||
                        n.getContent().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }
}