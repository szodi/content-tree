package org.ptc.contenttree.service;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.model.TreeNode;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Profile("dev")
public class InitServiceDev implements ApplicationRunner {

    private final TreeNodeService treeNodeService;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        TreeNode root = treeNodeService.createOrUpdate("root", "Root tree node", null, null);

        TreeNode child1 = treeNodeService.createOrUpdate("child1", "Child1 tree node", root.getId(), null);
        TreeNode child2 = treeNodeService.createOrUpdate("child2", "Child2 tree node", root.getId(), null);

        treeNodeService.createOrUpdate("grandChild1", "GrandChild1 tree node", child1.getId(), null);
        treeNodeService.createOrUpdate("grandChild2", "GrandChild2 tree node", child1.getId(), null);

        treeNodeService.createOrUpdate("grandChild3", "GrandChild3 tree node", child2.getId(), null);
        treeNodeService.createOrUpdate("grandChild4", "GrandChild4 tree node", child2.getId(), null);
    }
}
