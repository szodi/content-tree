package org.ptc.contenttree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ptc.contenttree.dto.TreeNodeDto;
import org.ptc.contenttree.service.TreeNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TreeNodeController.class)
class TreeNodeControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private TreeNodeService treeNodeService;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//
//    // ------------------------------------------------
//    // GET /nodes/  -> getRootTreeNode
//    // ------------------------------------------------
//    @Test
//    void getRootTreeNode_success() throws Exception {
//        TreeNodeDto dto = new TreeNodeDto();
//        dto.setId(1);
//        dto.setName("Root");
//
//        Mockito.when(treeNodeService.getRootTreeNode())
//                .thenReturn(dto);
//
//        mockMvc.perform(get("/nodes/")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.name").value("Root"));
//    }
//
//    @Test
//    void getRootTreeNode_notFound() throws Exception {
//        Mockito.when(treeNodeService.getRootTreeNode())
//                .thenReturn(null);
//
//        mockMvc.perform(get("/nodes/")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    // ------------------------------------------------
//    // POST /nodes/{parentNodeId} -> createTreeNode
//    // ------------------------------------------------
//    @Test
//    void createTreeNode_success() throws Exception {
//        TreeNodeDto input = new TreeNodeDto();
//        input.setName("Child");
//
//        TreeNodeDto output = new TreeNodeDto();
//        output.setId(2);
//        output.setParentId(1);
//        output.setName("Child");
//
//        Mockito.when(treeNodeService.createTreeNode(Mockito.eq(1), Mockito.any(TreeNodeDto.class)))
//                .thenReturn(output);
//
//        mockMvc.perform(post("/nodes/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(input)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(2))
//                .andExpect(jsonPath("$.parentId").value(1))
//                .andExpect(jsonPath("$.name").value("Child"));
//    }
//
//    @Test
//    void createTreeNode_notFound() throws Exception {
//        Mockito.when(treeNodeService.createTreeNode(Mockito.eq(1), Mockito.any(TreeNodeDto.class)))
//                .thenReturn(null);
//
//        mockMvc.perform(post("/nodes/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isNotFound());
//    }
//
//    // ------------------------------------------------
//    // PUT /nodes/ -> updateTreeNode
//    // ------------------------------------------------
//    @Test
//    void updateTreeNode_success() throws Exception {
//        TreeNodeDto input = new TreeNodeDto();
//        input.setId(5);
//        input.setName("Updated");
//
//        Mockito.when(treeNodeService.updateTreeNode(Mockito.any(TreeNodeDto.class)))
//                .thenReturn(input);
//
//        mockMvc.perform(put("/nodes/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(input)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(5))
//                .andExpect(jsonPath("$.name").value("Updated"));
//    }
//
//    @Test
//    void updateTreeNode_notFound() throws Exception {
//        Mockito.when(treeNodeService.updateTreeNode(Mockito.any(TreeNodeDto.class)))
//                .thenReturn(null);
//
//        mockMvc.perform(put("/nodes/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isNotFound());
//    }
//
//    // ------------------------------------------------
//    // DELETE /nodes/{nodeId} -> deleteTreeNode
//    // ------------------------------------------------
//    @Test
//    void deleteTreeNode_success() throws Exception {
//        Mockito.doNothing().when(treeNodeService).deleteTreeNode(10);
//
//        mockMvc.perform(delete("/nodes/10"))
//                .andExpect(status().isOk());
//
//        Mockito.verify(treeNodeService).deleteTreeNode(10);
//    }
//
//    // ------------------------------------------------
//    // GET /nodes/{nodeId} -> getTreeNode
//    // ------------------------------------------------
//    @Test
//    void getTreeNode_success() throws Exception {
//        TreeNodeDto dto = new TreeNodeDto();
//        dto.setId(3);
//        dto.setName("Node 3");
//
//        Mockito.when(treeNodeService.getTreeNodeWithChildren(3))
//                .thenReturn(dto);
//
//        mockMvc.perform(get("/nodes/3")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(3))
//                .andExpect(jsonPath("$.name").value("Node 3"));
//    }
//
//    @Test
//    void getTreeNode_notFound() throws Exception {
//        Mockito.when(treeNodeService.getTreeNodeWithChildren(3))
//                .thenReturn(null);
//
//        mockMvc.perform(get("/nodes/3")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    // ------------------------------------------------
//    // PUT /nodes/{nodeId}/{parentNodeId} -> relocateTreeNode
//    // ------------------------------------------------
//    @Test
//    void relocateTreeNode_success() throws Exception {
//        TreeNodeDto dto = new TreeNodeDto();
//        dto.setId(10);
//        dto.setParentId(2);
//        dto.setName("Moved");
//
//        Mockito.when(treeNodeService.relocateTreeNode(10, 2))
//                .thenReturn(dto);
//
//        mockMvc.perform(put("/nodes/10/2")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(10))
//                .andExpect(jsonPath("$.parentId").value(2))
//                .andExpect(jsonPath("$.name").value("Moved"));
//    }
//
//    @Test
//    void relocateTreeNode_notFound() throws Exception {
//        Mockito.when(treeNodeService.relocateTreeNode(10, 2))
//                .thenReturn(null);
//
//        mockMvc.perform(put("/nodes/10/2")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    // ------------------------------------------------
//    // Validation / path errors
//    // ------------------------------------------------
//    @Test
//    void invalidPathVariables() throws Exception {
//        mockMvc.perform(get("/nodes/a"))
//                .andExpect(status().isBadRequest());
//
//        mockMvc.perform(put("/nodes/a/b"))
//                .andExpect(status().isBadRequest());
//    }
}