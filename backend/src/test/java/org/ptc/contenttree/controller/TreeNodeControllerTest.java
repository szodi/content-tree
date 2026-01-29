package org.ptc.contenttree.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.service.TreeNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TreeNodeController.class)
class TreeNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TreeNodeService service;

    /* ---------- /move ---------- */

    @Test
    void move_shouldReturnMovedTreeNodes() throws Exception {
        Long nodeId = 1L;
        Long newParentId = 2L;

        TreeNode node = new TreeNode();
        node.setId(nodeId);

        Mockito.when(service.move(nodeId, newParentId))
                .thenReturn(List.of(node));

        mockMvc.perform(put("/api/tree/move/{nodeId}/{newParentNodeId}", nodeId, newParentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    /* ---------- /content/{id} ---------- */

    @Test
    void load_shouldReturnContentMap() throws Exception {
        Long id = 5L;
        String content = "node content";

        Mockito.when(service.loadContent(id)).thenReturn(content);

        mockMvc.perform(get("/api/tree/content/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content));
    }

    /* ---------- /search ---------- */

    @Test
    void search_shouldReturnMatchingNodes() throws Exception {
        String text = "abc";

        TreeNode node1 = new TreeNode();
        node1.setId(1L);

        TreeNode node2 = new TreeNode();
        node2.setId(2L);

        Mockito.when(service.search(text))
                .thenReturn(List.of(node1, node2));

        mockMvc.perform(get("/api/tree/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    /* ---------- Negative / edge tests ---------- */

    @Test
    void search_shouldReturnEmptyList_whenNoResults() throws Exception {
        Mockito.when(service.search("none")).thenReturn(List.of());

        mockMvc.perform(get("/api/tree/search").param("text", "none"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void load_shouldHandleNullContent() throws Exception {
        Long id = 10L;
        Mockito.when(service.loadContent(any())).thenReturn(null);

        mockMvc.perform(get("/api/tree/content/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(""));
    }
}
