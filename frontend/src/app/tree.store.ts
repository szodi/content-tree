import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {TreeNode} from '@ptc-api-models/treeNode';

type TreeState = {
  nodes: TreeNode[] | null;
  selectedNode: TreeNode | null;
}

const initialState: TreeState = {
  nodes: null,
  selectedNode: null
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
      const nodes = deleteNode(store.nodes()!, node)
      patchState(store, { nodes })
    },
    setSelectedNode(treeNode: TreeNode) {
      patchState(store, { selectedNode: treeNode })
    },
    // addNode(treeNode: TreeNodeDto, parentId: number) {
    //   const rootNode = store.rootNode()!;
    //   console.log('before', rootNode)
    //   addNode(rootNode!, treeNode, parentId);
    //   console.log('after', rootNode)
    //   patchState(store, { rootNode: { ...rootNode} })
    // },
    // updateNode(treeNode: TreeNodeDto) {
    //   const rootNode = store.rootNode()!;
    //   updateNode(rootNode!, treeNode);
    //   patchState(store, { rootNode: { ...rootNode} })
    // }
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
  const parentIndex = nodesClone.findIndex(n => n.id === node.parentId);
  nodesClone[parentIndex] = {
    ...nodesClone[parentIndex],
    childrenIds: nodesClone[parentIndex].childrenIds!.filter(id => id !== node.id)
  };
  return nodesClone;
}
