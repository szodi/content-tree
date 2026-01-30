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
@Profile("!dev")
public class InitService implements ApplicationRunner {

    private final TreeNodeService treeNodeService;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        treeNodeService.createOrUpdate("root", "Root tree node", null, null);
    }
}
