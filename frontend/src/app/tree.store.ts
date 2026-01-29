import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {TreeNode} from '@ptc-api-models/treeNode';

type TreeState = {
  nodes: TreeNode[] | null;
  selectedNode: TreeNode | null;
  filteredNodes: TreeNode[] | null;
}

const initialState: TreeState = {
  nodes: null,
  selectedNode: null,
  filteredNodes: null
}

export const TreeStore = signalStore(
  { providedIn: 'root'},
  withState(initialState),
  withMethods(store => ({
    setNodes(nodes: TreeNode[]) {
      const nodesClone = cloneNodes(nodes);
      patchState(store, { nodes: nodesClone })
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
      patchState(store, { filteredNodes: nodes })
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
