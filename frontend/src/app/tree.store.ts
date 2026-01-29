import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {TreeNode} from '@ptc-api-models/treeNode';

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
  withMethods(store => ({
    setNodes(nodes: TreeNode[]) {
      patchState(store, { nodes })
    },
    addNode(node: TreeNode) {
      const nodes = addNode(store.nodes()!, node)
      patchState(store, { nodes })
    },
    updateNode(node: TreeNode) {
      const nodeIndex = store.nodes()!.findIndex(n => n.id === node.id);
      const nodesClone = cloneNodes(store.nodes()!) as TreeNode[];
      nodesClone.find(n => n.id === node.parentId)?.childrenIds!.push(node.id!);
      nodesClone[nodeIndex] = node;
      patchState(store, { nodes: nodesClone })
    },
    deleteNode(node: TreeNode) {
      const nodes = deleteNode(cloneNodes(store.nodes()!), node)
      const parentIndex = nodes.findIndex(n => n.id === node.parentId);
      nodes[parentIndex] = {
        ...nodes[parentIndex],
        childrenIds: nodes[parentIndex].childrenIds!.filter(id => id !== node.id)
      };
      patchState(store, { nodes })
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

function deleteNode(nodes: TreeNode[], node: TreeNode) {
  const nodesClone = nodes.filter(n => n.id !== node.id);
  node.childrenIds?.forEach(childId => deleteNode(nodesClone, nodesClone.find(n => n.id === childId)!));
  return nodesClone;
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
