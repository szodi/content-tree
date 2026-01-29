package org.ptc.contenttree.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "node")
@SequenceGenerator(name = "seq_node", allocationSize = 5)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreeNode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_node")
    @ToString.Include
    Integer id;

    @Column(length = 50)
    String name;

    @Column
    String content;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "node_relation",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<TreeNode> children = new ArrayList<>();

    @ManyToMany(mappedBy = "children", cascade = {CascadeType.ALL})
    private List<TreeNode> parents;
}
