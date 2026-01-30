import {patchState, signalStore, withComputed, withMethods, withState} from '@ngrx/signals';
import {TreeNode} from '@ptc-api-models/treeNode';
import {computed} from '@angular/core';

type TreeState = {
  nodes: TreeNode[] | null;
  selectedNode: TreeNode | null;
  filteredNodes: TreeNode[];
  halfhiglighted: TreeNode[];
}

const initialState: TreeState = {
  nodes: null,
  selectedNode: null,
  filteredNodes: [],
  halfhiglighted: []
}

export const TreeStore = signalStore(
  { providedIn: 'root'},
  withState(initialState),
  withComputed(store => ({
    rootNode: computed(() => store.nodes()?.find(node => !node.parentId)),
  })),
  withMethods(store => ({
    setNodes(nodes: TreeNode[]) {
      patchState(store, { nodes })
    },
    addNode(node: TreeNode) {
      const nodes = addNode(store.nodes()!, node)
      patchState(store, { nodes })
    },
    moveNode(node: TreeNode) {
      const nodeIndex = store.nodes()!.findIndex(n => n.id === node.id);
      const nodesClone = cloneNodes(store.nodes()!) as TreeNode[];
      nodesClone.find(n => n.id === node.parentId)?.childrenIds!.push(node.id!);
      nodesClone[nodeIndex] = node;
      patchState(store, { nodes: nodesClone })
    },
    updateNode(node: TreeNode) {
      const nodeIndex = store.nodes()!.findIndex(n => n.id === node.id);
      const nodesClone = cloneNodes(store.nodes()!) as TreeNode[];
      nodesClone[nodeIndex] = node;
      patchState(store, { nodes: nodesClone })
    },
    deleteNode(node: TreeNode) {
      const nodes = store.nodes()!.map(n => {
        if (n.childrenIds?.includes(node.id!)) {
          return {...n, childrenIds: n.childrenIds!.filter(id => id !== node.id)}
        }
        return n;
      })
      const toDelete: number[] = [];
      collectNodesToDelete(nodes, node, toDelete);
      const deleted = nodes.filter(n => !toDelete.includes(n.id!));
      patchState(store, { nodes: deleted })
    },
    setSelectedNode(treeNode: TreeNode) {
      patchState(store, { selectedNode: treeNode })
    },
    filterNodes(nodes: TreeNode[]) {
      const half = collectParentsWithHighlightedDescendants(store.nodes()!, nodes)
      patchState(store, { filteredNodes: nodes, halfhiglighted: half })
    }
  }))
);

function cloneNodes(nodes: TreeNode[]): TreeNode[] {
  return JSON.parse(JSON.stringify(nodes)) as TreeNode[];
}

function addNode(nodes: TreeNode[], node: TreeNode) {
  const nodesClone = cloneNodes(nodes);
  nodesClone.find(n => n.id === node.parentId)?.childrenIds!.push(node.id!);
  nodesClone.push(node);
  return nodesClone;
}

function collectNodesToDelete(nodes: TreeNode[], node: TreeNode, toDelete: number[]) {
  toDelete.push(node.id!);
  node.childrenIds?.forEach(childId => collectNodesToDelete(nodes, nodes.find(n => n.id === childId)!, toDelete));
}

function collectParentsWithHighlightedDescendants(nodes: TreeNode[], filteredNodes: TreeNode[]) {
  // 1) Build lookup map
  const nodeMap = new Map<number, TreeNode>();
  for (const node of nodes) {
    nodeMap.set(node.id!, node);
  }

  const result = new Set<number>(); // store parent node IDs

  // 2) Find all highlighted nodes
  const highlightedNodes = nodes.filter(n => filteredNodes.some(fn => fn.id === n.id));

  // 3) For each highlighted node, walk up the tree
  for (const node of highlightedNodes) {
    let current = node;

    while (current.parentId != null) {
      const parent = nodeMap.get(current.parentId);
      if (!parent) break;

      result.add(parent.id!);   // collect parent
      current = parent;        // move up
    }
  }

  // 4) Return actual node objects (or IDs if preferred)
  return [...result].map(id => nodeMap.get(id)!);
}
