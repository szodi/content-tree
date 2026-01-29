package org.ptc.contenttree.service;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.model.TreeNode;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class InitService implements ApplicationRunner {

    private final TreeNodeService treeNodeService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
//        TreeNode root = TreeNode.builder()
//                .name("root")
//                .content("Root tree node")
//                .build();

        TreeNode root = treeNodeService.createOrUpdate("root", "Root tree node", null, null);

        TreeNode child1 = treeNodeService.createOrUpdate("child1", "Child1 tree node", root.getId(), null);
        TreeNode child2 = treeNodeService.createOrUpdate("child2", "Child2 tree node", root.getId(), null);

        treeNodeService.createOrUpdate("grandChild1", "GrandChild1 tree node", child1.getId(), null);
        treeNodeService.createOrUpdate("grandChild2", "GrandChild2 tree node", child1.getId(), null);

        treeNodeService.createOrUpdate("grandChild3", "GrandChild3 tree node", child2.getId(), null);
        treeNodeService.createOrUpdate("grandChild4", "GrandChild4 tree node", child2.getId(), null);
//
//        TreeNode child1 = persist("child1", "Child1 tree node");
//        TreeNode grandChild1 = persist("grandChild1", "GrandChild1 tree node");
//        TreeNode grandChild2 = persist("grandChild2", "GrandChild2 tree node");
//
//        treeNodeService.createOrUpdate()
//        child1.getChildren().add(grandChild1);
//        child1.getChildren().add(grandChild2);
//
//        TreeNode child2 = persist("child2", "Child2 tree node");
//        TreeNode grandChild3 = persist("grandChild3", "GrandChild3 tree node");
//        TreeNode grandChild4 = persist("grandChild4", "GrandChild4 tree node");
//        child2.getChildren().add(grandChild3);
//        child2.getChildren().add(grandChild4);
//
//        root.getChildren().add(child1);
//        root.getChildren().add(child2);
//        treeNodeRepository.save(root);
    }

//    private TreeNode persist(String name, String content) {
//        return treeNodeRepository.save(TreeNode.builder()
//                .name(name)
//                .content(content)
//                .children(new ArrayList<>())
//                .build());
//    }
}
