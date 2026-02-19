package org.ptc.contenttree.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("!dev")
public class InitService implements ApplicationRunner {

    private final TreeNodeService treeNodeService;

    @Override
    public void run(ApplicationArguments args) {
        if (treeNodeService.getAllNodes().isEmpty()) {
            treeNodeService.createOrUpdate("root", "Root tree node", null, null);
        }
    }
}
