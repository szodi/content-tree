import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {TreeNodeDto} from '@ptc-api-models/treeNodeDto';

type TreeNodeState = {
  rootNode: TreeNodeDto | null;
  selectedNode: TreeNodeDto | null;
}

const initialState: TreeNodeState = {
  rootNode: null,
  selectedNode: null
}

export const TreeNodeStore = signalStore(
  { providedIn: 'root'},
  withState(initialState),
  withMethods(store => ({
    setTreeNode(rootNode: TreeNodeDto) {
      patchState(store, { rootNode: { ...rootNode} })
    },
    setSelectedNode(treeNode: TreeNodeDto) {
      patchState(store, { selectedNode: treeNode })
    },
    addNode(treeNode: TreeNodeDto, parentId: number) {
      const rootNode = store.rootNode()!;
      console.log('before', rootNode)
      addNode(rootNode!, treeNode, parentId);
      console.log('after', rootNode)
      patchState(store, { rootNode: { ...rootNode} })
    },
    updateNode(treeNode: TreeNodeDto) {
      const rootNode = store.rootNode()!;
      updateNode(rootNode!, treeNode);
      patchState(store, { rootNode: { ...rootNode} })
    }
  }))
);

function addNode(rootNode: TreeNodeDto, treeNode: TreeNodeDto, parentId: number) {
  if (rootNode.id === parentId) {
    rootNode.children?.push(treeNode);
    return;
  }
  rootNode.children?.forEach(child => addNode(child, treeNode, parentId));
}

function updateNode(rootNode: TreeNodeDto, treeNode: TreeNodeDto) {
  if (rootNode.id === treeNode.id) {
    rootNode = {...treeNode};
    return;
  }
  rootNode.children?.forEach(child => updateNode(child, treeNode));
}
