package org.ptc.contenttree.service;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.repository.TreeNodeRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class InitService implements ApplicationRunner {

    private final TreeNodeRepository treeNodeRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        TreeNode root = persist("root", "Root tree node");

        TreeNode child1 = persist("child1", "Child1 tree node");
        TreeNode grandChild1 = persist("grandChild1", "GrandChild1 tree node");
        TreeNode grandChild2 = persist("grandChild2", "GrandChild2 tree node");
        child1.getChildren().add(grandChild1);
        child1.getChildren().add(grandChild2);

        TreeNode child2 = persist("child2", "Child2 tree node");
        TreeNode grandChild3 = persist("grandChild3", "GrandChild3 tree node");
        TreeNode grandChild4 = persist("grandChild4", "GrandChild4 tree node");
        child2.getChildren().add(grandChild3);
        child2.getChildren().add(grandChild4);

        root.getChildren().add(child1);
        root.getChildren().add(child2);
        treeNodeRepository.save(root);
    }

    private TreeNode persist(String name, String content) {
        return treeNodeRepository.save(TreeNode.builder()
                .name(name)
                .content(content)
                .children(new ArrayList<>())
                .build());
    }
}
