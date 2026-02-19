package org.ptc.contenttree.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.repository.TreeNodeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreeNodeServiceTest {

    private TreeNodeRepository repository;
    private TreeNodeService service;

    @BeforeEach
    void setUp() {
        repository = mock(TreeNodeRepository.class);
        service = new TreeNodeService(repository);
    }

    @Test
    void createOrUpdate_shouldRejectSelfParent() {
        assertThatThrownBy(() -> service.createOrUpdate("name", "content", 7L, 7L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("itself");
    }

    @Test
    void createOrUpdate_shouldUpdateParentChildrenWhenNewNodeCreated() {
        TreeNode parent = TreeNode.builder().id(1L).name("p").content("p").childrenIds(new ArrayList<>()).build();

        when(repository.findById(1L)).thenReturn(Optional.of(parent));
        when(repository.save(any(TreeNode.class))).thenAnswer(invocation -> {
            TreeNode arg = invocation.getArgument(0);
            if (arg.getId() == null) {
                arg.setId(2L);
            }
            return arg;
        });

        TreeNode created = service.createOrUpdate("child", "child-content", 1L, null);

        assertThat(created.getId()).isEqualTo(2L);
        assertThat(parent.getChildrenIds()).contains(2L);
    }

    @Test
    void move_shouldFailWhenParentMissing() {
        TreeNode node = TreeNode.builder().id(10L).name("n").content("c").build();
        when(repository.findById(10L)).thenReturn(Optional.of(node));
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.move(10L, 99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Parent node not found");
    }

    @Test
    void search_shouldFilterByNameOrContent_caseInsensitive() {
        TreeNode a = TreeNode.builder().id(1L).name("Hello Node").content("something").build();
        TreeNode b = TreeNode.builder().id(2L).name("Other").content("HELLO content").build();
        TreeNode c = TreeNode.builder().id(3L).name("none").content("zzz").build();

        when(repository.findAll()).thenReturn(List.of(a, b, c));

        List<TreeNode> result = service.search("hello");

        assertThat(result).extracting(TreeNode::getId).containsExactlyInAnyOrder(1L, 2L);
    }
}
