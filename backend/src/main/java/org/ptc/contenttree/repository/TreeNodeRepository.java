package org.ptc.contenttree.repository;

import org.ptc.contenttree.model.TreeNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreeNodeRepository extends JpaRepository<TreeNode, Integer> {

    @Query("""
        SELECT n FROM TreeNode n
        WHERE n.parents IS EMPTY
    """)
//    @Query("""
//        select distinct tn from TreeNode tn
//        left join tn.children c
//        where c is null
//    """)
    TreeNode findRootNode();

    @Query("""
        select distinct tn from TreeNode tn
        left join fetch tn.children
        where tn.id = :nodeId
    """)
    Optional<TreeNode> findByNodeId(Integer nodeId);

    @Query("""
        select distinct tn from TreeNode tn
        left join fetch tn.children
        where tn.id in (:ids)
    """)
    List<TreeNode> findNodes(List<Integer> ids);

    @Query("""
        select distinct tn from TreeNode tn
        left join fetch tn.children cr
        where cr.id = :nodeId
     """)
    Optional<TreeNode> findByChildId(Integer nodeId);
}
