package org.ptc.contenttree.repository;

import org.ptc.contenttree.model.TreeNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeNodeRepository extends JpaRepository<TreeNode, Long> {
}
