package org.ptc.contenttree.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "node_relation")
@IdClass(TreeNodeRelationId.class)
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeRelation {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "parent_id")
    TreeNode parent;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "child_id")
    TreeNode child;
}
