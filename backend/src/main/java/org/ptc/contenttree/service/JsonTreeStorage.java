package org.ptc.contenttree.service;

import jakarta.annotation.PostConstruct;
import org.ptc.contenttree.model.TreeNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class JsonTreeStorage {

    @Value( "${ptc.storage.file.path}")
    private String filePath = "data/tree.json";

    private Path FILE;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<Long, TreeNode> cache = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void init() {
        this.FILE = Paths.get(filePath);
        try {
            if (Files.exists(FILE)) {
                List<TreeNode> nodes = Arrays.asList(mapper.readValue(FILE.toFile(), TreeNode[].class));
                nodes.forEach(n -> {
                    cache.put(n.getId(), n);
                    idGen.set(Math.max(idGen.get(), n.getId() + 1));
                });
            } else {
                Files.createDirectories(FILE.getParent());
                save();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    synchronized void save() {
        mapper.writerWithDefaultPrettyPrinter().writeValue(FILE.toFile(), cache.values());
    }

    public synchronized TreeNode createOrUpdate(TreeNode node) {
        if (node.getId() == null) {
            node.setId(getNextId());
        }
        cache.put(node.getId(), node);
        save();
        return node;
    }

    public synchronized void delete(Long id) {
        Collection<TreeNode> all = findAll();
        all.forEach(n -> n.getChildrenIds().remove(id));
        TreeNode node = cache.get(id);
        if (node == null) return;

        List<Long> childrenIds = new ArrayList<>(node.getChildrenIds());
        for (Long childId : childrenIds) {
            delete(childId);
        }
        cache.remove(id);
    }

    public Long getNextId() {
        return idGen.getAndIncrement();
    }

    public Collection<TreeNode> findAll() {
        return cache.values();
    }

    public TreeNode findById(Long id) {
        return cache.get(id);
    }
}