import {Component, computed, effect, ElementRef, HostListener, inject, signal, viewChildren} from '@angular/core';
import {MyNode} from '../my-node/my-node';
import {TreeNodeControllerService} from '@ptc-api-services/treeNodeController.service';
import {TreeStore} from '../../tree.store';
import {TreeNode} from '@ptc-api-models/treeNode';

interface Point {
  x: number;
  y: number;
}

interface TreeNodeBlock {
  treeNode: TreeNode;
  component: MyNode;
  element: ElementRef<HTMLElement>;
  position: Point;
}

@Component({
  selector: 'app-my-node-tree',
  imports: [MyNode],
  templateUrl: './my-node-tree.html',
  styleUrl: './my-node-tree.scss',
})
export class MyNodeTree {

  private verticalGap = 50;
  private horizontalGap = 50;

  treeNodeService = inject(TreeNodeControllerService);
  treeStore = inject(TreeStore);

  nodes = this.treeStore.nodes;

  rootNode = computed(() => this.nodes()?.find(node => !node.parentId));
  selectedNode = this.treeStore.selectedNode;
  filteredNodes = this.treeStore.filteredNodes;

  boxElements = viewChildren(MyNode, { read: ElementRef });
  boxComps = viewChildren(MyNode);

  blocks = computed(() =>
    this.boxElements().map((element, index) => {
      return {
        component: this.boxComps()[index],
        treeNode: this.boxComps()[index].treeNode,
        element: element,
        position: { x: 0, y: 0 } as Point,
      } as TreeNodeBlock
    })
  );

  draggingBoxBlock = signal<TreeNodeBlock | null>(null);
  draggingBoxOffset = signal<Point>({x: 0, y: 0});

  subtree = computed(() => {
    if (this.blocks() && this.selectedNode()) {
      const subtree: TreeNodeBlock[] = [];
      this.collectChildNodes(this.selectedNode()!, subtree);
      return subtree;
    }
    return [];
  })

  offsets: Point[] = [];

  constructor() {
    effect(() => {
      if (this.nodes()?.length && this.blocks()?.length) {
        this.relocateComponents();
      }
    });
  }

  isFiltered(node: TreeNode) {
    return this.filteredNodes()?.find(filteredNode => filteredNode.id === node.id) !== undefined;
  }

  selectNode(node: TreeNode) {
    this.treeStore.setSelectedNode(node);
  }

  startDrag(event: MouseEvent, node: TreeNode) {
    this.selectNode(node);
    if (node === this.rootNode()) return;
    this.draggingBoxBlock.set(this.findBlock(node.id!));
    this.draggingBoxBlock()!.component.dragging = true;
    this.draggingBoxBlock()!.element.nativeElement.style.zIndex = '1000';
    this.draggingBoxBlock()!.element.nativeElement.style.transition = 'none';
    const draggingBoxBounds = this.draggingBoxBlock()!.element.nativeElement.getBoundingClientRect();
    this.draggingBoxOffset.set({
      x: event.clientX - draggingBoxBounds.x,
      y: event.clientY - draggingBoxBounds.y
    });
    this.offsets = [];
    this.subtree().forEach(subtreeNodeBlock => {
      subtreeNodeBlock.component.dragging = true;
      subtreeNodeBlock.element.nativeElement.style.zIndex = '1000';
      subtreeNodeBlock.element.nativeElement.style.transition = 'none';
      const nodeBounds = subtreeNodeBlock.element.nativeElement.getBoundingClientRect();
      this.offsets.push({
        x: event.clientX - nodeBounds.x,
        y: event.clientY - nodeBounds.y
      })
    });
    console.log(this.subtree());
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(event: MouseEvent) {
    event.preventDefault();
    if (!this.draggingBoxBlock()) return;

    this.setComponentPosition(this.draggingBoxBlock()!, {
      x: event.clientX - this.draggingBoxOffset().x,
      y: event.clientY - this.draggingBoxOffset().y
    })

    this.subtree().forEach((node, index) => {
      this.setComponentPosition(node, {
        x: event.clientX - this.offsets[index].x,
        y: event.clientY - this.offsets[index].y
      })
    });
    this.detectOverlaps();
  }

  @HostListener('document:mouseup')
  onMouseUp() {
    if (this.draggingBoxBlock()) {
      this.draggingBoxBlock()!.component.dragging = false;
      this.draggingBoxBlock()!.element.nativeElement.style.zIndex = '1';
      this.draggingBoxBlock()!.element.nativeElement.style.transition = 'top 0.2s ease-out, left 0.2s ease-out';
      this.subtree().forEach(node => {
        node.component.dragging = false;
        node.element.nativeElement.style.zIndex = '1';
        node.element.nativeElement.style.transition = 'top 0.2s ease-out, left 0.2s ease-out';
      });

      const targetNode = this.blocks().find(compRef => compRef.component.isOverlapped)?.component.treeNode;
      this.blocks().forEach(b => b.component.isOverlapped = false);
      if (targetNode) {
        this.treeNodeService.move(this.draggingBoxBlock()?.component.treeNode!.id!, targetNode.id!).subscribe(node => {
          // this.treeStore.setNodes(node)
          // this.initComponents()
        });
      } else {
        this.blocks().forEach(block => this.setComponentPosition(block, block.position));
      }

      this.draggingBoxBlock.set(null);
    }
  }

  private detectOverlaps() {
    if (!this.draggingBoxBlock()) return;
    const draggingBoxBounds = this.draggingBoxBlock()?.element.nativeElement.getBoundingClientRect()!;
    this.blocks().forEach(block => {
      if (block.component === this.draggingBoxBlock()!.component) {
        block.component.isOverlapped = false;
        return;
      }
      const componentBounds = block.element.nativeElement.getBoundingClientRect();
      block.component.isOverlapped = !(
        draggingBoxBounds.x + draggingBoxBounds.width < componentBounds.x ||
        draggingBoxBounds.x > componentBounds.x + componentBounds.width ||
        draggingBoxBounds.y + draggingBoxBounds.height < componentBounds.y ||
        draggingBoxBounds.y > componentBounds.y + componentBounds.height
      );
    });
  }

  private setComponentPosition(block: TreeNodeBlock, position: Point) {
    block.element.nativeElement.style.left = `${position.x}px`;
    block.element.nativeElement.style.top = `${position.y}px`;
  }

  private collectChildNodes(node: TreeNode, subtree: TreeNodeBlock[]) {
    node.childrenIds?.forEach(child => {
      const childBlock = this.findBlock(child)!;
      subtree.push(childBlock);
      this.collectChildNodes(childBlock.treeNode, subtree)
    });
  }

  relocateComponents() {
    this.blocks().forEach(block => block.position = {x: 0, y: 0});
    const rootBlock = this.blocks().find(block => block.treeNode.id === this.rootNode()!.id)!;
    const relocatedBlocks: TreeNodeBlock[] = [];
    this.relocate(rootBlock.treeNode, {x: 0, y: 0}, relocatedBlocks);
    relocatedBlocks.forEach(block => {
      block.element.nativeElement.style.left = `${block.position.x}px`;
      block.element.nativeElement.style.top = `${block.position.y}px`;
    });
  }

  private relocate(node: TreeNode, offset: Point, relocatedBlocks: TreeNodeBlock[]) {
    const nodeBlock = this.findBlock(node.id!);
    nodeBlock.position = {
      x: nodeBlock.position.x + offset.x,
      y: nodeBlock.position.y + offset.y
    }
    relocatedBlocks.push(nodeBlock);
    node.childrenIds!.forEach(child => this.relocate(this.findBlock(child).treeNode, {
      x: nodeBlock.position.x + this.horizontalGap,
      y: this.verticalGap * relocatedBlocks.length
    }, relocatedBlocks));
  }

  private findBlock(nodeId: number): TreeNodeBlock{
    return this.blocks().find(block => block.treeNode.id === nodeId)!;
  }
}
