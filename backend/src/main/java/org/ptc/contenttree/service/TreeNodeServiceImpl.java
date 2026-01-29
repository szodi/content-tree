package org.ptc.contenttree.service;

import lombok.RequiredArgsConstructor;
import org.ptc.contenttree.dto.TreeNodeDto;
import org.ptc.contenttree.exception.NodeNotFoundException;
import org.ptc.contenttree.mapper.TreeNodeMapper;
import org.ptc.contenttree.model.TreeNode;
import org.ptc.contenttree.repository.TreeNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TreeNodeServiceImpl implements TreeNodeService {

    private final TreeNodeRepository treeNodeRepository;
    private final TreeNodeMapper treeNodeMapper;

    @Override
    public TreeNodeDto getRootTreeNode() {
        TreeNode treeNode = treeNodeRepository.findRootNode();
        return treeNodeMapper.convert(treeNode);
    }

    @Override
    public TreeNodeDto getTreeNodeWithChildren(Integer rootNodeId) {
        TreeNode treeNode = treeNodeRepository.findByNodeId(rootNodeId).orElseThrow(NodeNotFoundException::new);
        return treeNodeMapper.convert(treeNode);
    }

    @Override
    @Transactional
    public TreeNodeDto createTreeNode(Integer parentNodeId, TreeNodeDto treeNodeDto) {
        TreeNode parentTreeNode = treeNodeRepository.findById(parentNodeId).orElseThrow(NodeNotFoundException::new);
        TreeNode treeNode = TreeNode.builder()
                .name(treeNodeDto.getName())
                .content(treeNodeDto.getContent())
                .parents(List.of(parentTreeNode))
                .build();
        treeNodeRepository.save(treeNode);
        parentTreeNode.getChildren().add(treeNode);
        treeNodeRepository.save(parentTreeNode);
        return treeNodeMapper.convert(treeNode);
    }

    @Override
    @Transactional
    public TreeNodeDto updateTreeNode(TreeNodeDto treeNodeDto) {
        TreeNode treeNode = treeNodeRepository.findById(treeNodeDto.getId()).orElseThrow(NodeNotFoundException::new);
        treeNode.setName(treeNodeDto.getName());
        treeNode.setContent(treeNodeDto.getContent());
        treeNode = treeNodeRepository.save(treeNode);
        return treeNodeMapper.convert(treeNode);
    }

    @Override
    public TreeNodeDto getTreeNode(Integer nodeId) {
        TreeNode treeNode = treeNodeRepository.findById(nodeId).orElseThrow(NodeNotFoundException::new);
        return treeNodeMapper.convert(treeNode);
    }

    @Override
    @Transactional
    public TreeNodeDto relocateTreeNode(Integer nodeId, Integer newParentNodeId) {
        TreeNode treeNode = treeNodeRepository.findById(nodeId).orElseThrow(NodeNotFoundException::new);

        TreeNode oldParentNode = treeNodeRepository.findByChildId(nodeId).orElse(null);
        if (oldParentNode != null) {
            oldParentNode.getChildren().remove(treeNode);
            treeNodeRepository.save(oldParentNode);
        }

        TreeNode newParentNode = treeNodeRepository.findById(newParentNodeId).orElseThrow(NodeNotFoundException::new);
        newParentNode.getChildren().add(treeNode);

        treeNodeRepository.save(newParentNode);

        return treeNodeMapper.convert(newParentNode);
    }

    @Override
    @Transactional
    public void deleteTreeNode(Integer nodeId) {
        TreeNode treeNode = treeNodeRepository.findById(nodeId).orElseThrow(NodeNotFoundException::new);
//        treeNode.getParent().getChildren().remove(treeNode);
        treeNodeRepository.delete(treeNode);
    }
}
