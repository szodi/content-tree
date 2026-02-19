package org.ptc.contenttree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.repository.TreeNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TreeNodeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TreeNodeRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void createAndLoad_shouldPersistInH2Database() throws Exception {
        TreeNode root = repository.save(TreeNode.builder().name("root").content("root-content").build());

        String payload = objectMapper.writeValueAsString(Map.of(
                "name", "child",
                "content", "child-content",
                "parentId", root.getId()
        ));

        mockMvc.perform(post("/api/tree/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.parentId").value(root.getId()));

        mockMvc.perform(get("/api/tree/search").param("text", "child"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("child"));
    }

    @Test
    void create_shouldValidateRequestBody() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "name", "",
                "content", ""
        ));

        mockMvc.perform(post("/api/tree/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}
