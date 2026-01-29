import {Component, ComponentRef, computed, effect, HostListener, inject, signal, ViewContainerRef} from '@angular/core';
import {TreeNodeStore} from '../../app.store';
import {MyNode} from '../my-node/my-node';
import {TreeNodeDto} from '@ptc-api-models/treeNodeDto';
import {TreeNodeControllerService} from '@ptc-api-services/treeNodeController.service';

interface Point {
  x: number;
  y: number;
}

@Component({
  selector: 'app-my-node-tree',
  imports: [],
  templateUrl: './my-node-tree.html',
  styleUrl: './my-node-tree.scss',
})
export class MyNodeTree {

  private verticalGap = 50;
  private horizontalGap = 50;

  treeNodeStore = inject(TreeNodeStore);

  treeNodeService = inject(TreeNodeControllerService);

  rootNode = this.treeNodeStore.rootNode;
  selectedNode = this.treeNodeStore.selectedNode;

  draggingBox = signal<ComponentRef<MyNode> | null>(null);
  draggingBoxOffset = signal<Point>({x: 0, y: 0});

  nodeComponents = signal<ComponentRef<MyNode>[]>([]);

  subtree = computed(() => {
    if (this.nodeComponents() && this.selectedNode()) {
      const subtree: ComponentRef<MyNode>[] = [];
      this.collectChildNodes(this.selectedNode()!, subtree);
      return subtree;
    }
    return [];
  })

  viewContainer = inject(ViewContainerRef);
  offsets: Point[] = [];

  constructor() {
    effect(() => {
      if (this.rootNode() && !this.nodeComponents().length) {
        this.initComponents();
      }
    });
    effect(() => {
      if (this.selectedNode() && this.nodeComponents().length) {
        this.nodeComponents().forEach(compRef => compRef.instance.isSelected = compRef.instance.treeNode?.id === this.selectedNode()!.id);
      }
    });
  }

  private initComponents() {
    let comps: ComponentRef<MyNode>[] = [];
    this.createComponentRefList(this.rootNode() ?? {}, comps);
    this.nodeComponents.set(comps);
    this.setPositions(this.rootNode()!, [], 0);
  }

  selectNode(node: TreeNodeDto) {
    this.treeNodeStore.setSelectedNode(node);
  }

  startDrag(event: MouseEvent, box: ComponentRef<MyNode>) {
    this.selectNode(box.instance.treeNode!);
    if (box.instance.treeNode === this.rootNode()) return;
    this.draggingBox.set(box);
    this.draggingBox()!.location.nativeElement.style.zIndex = '1000';
    this.draggingBox()!.location.nativeElement.classList.add('no-select');
    const draggingBoxBounds = this.draggingBox()?.location.nativeElement.getBoundingClientRect();
    this.draggingBoxOffset.set({
      x: event.clientX - draggingBoxBounds.x,
      y: event.clientY - draggingBoxBounds.y
    });
    this.offsets = [];
    this.subtree().forEach(node => {
      node.location.nativeElement.style.zIndex = '1000';
      const nodeBounds = node.location.nativeElement.getBoundingClientRect();
      this.offsets.push({
        x: event.clientX - nodeBounds.x,
        y: event.clientY - nodeBounds.y
      })
    });
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(event: MouseEvent) {
    event.preventDefault();
    if (!this.draggingBox()) return;
    this.setComponentPosition(this.draggingBox()!, {
      x: event.clientX - this.draggingBoxOffset().x,
      y: event.clientY - this.draggingBoxOffset().y
    });
    this.subtree().forEach((node, index) => {
      this.setComponentPosition(node, {
        x: event.clientX - this.offsets[index].x,
        y: event.clientY - this.offsets[index].y
      });
    });
    this.detectOverlaps();
  }

  @HostListener('document:mouseup')
  onMouseUp() {
    if (this.draggingBox()) {
      this.draggingBox()!.location.nativeElement.style.zIndex = '1';
      this.draggingBox()!.location.nativeElement.classList.remove('no-select');
      this.subtree().forEach(node => node.location.nativeElement.style.zIndex = '1');
    }
    const targetNode = this.nodeComponents().find(compRef => compRef.instance.isOverlapped)?.instance;
    this.nodeComponents().forEach(b => b.instance.isOverlapped = false);
    if (targetNode) {
      this.treeNodeService.relocateTreeNode(this.draggingBox()?.instance.treeNode!.id!, targetNode.treeNode!.id!).subscribe(node => this.treeNodeStore.setTreeNode(node));
    }
    this.setPositions(this.rootNode()!, [], 0);
    this.draggingBox.set(null);
  }

  private detectOverlaps() {
    if (!this.draggingBox()) return;

    const draggingBoxBounds = this.draggingBox()?.location.nativeElement.getBoundingClientRect();

    this.nodeComponents().forEach(compRef => {
      const b = compRef.instance;
      if (b === this.draggingBox()!.instance) {
        b.isOverlapped = false;
        return;
      }

      const componentBounds = compRef.location.nativeElement.getBoundingClientRect();

      b.isOverlapped = !(
        draggingBoxBounds.x + draggingBoxBounds.width < componentBounds.x ||
        draggingBoxBounds.x > componentBounds.x + componentBounds.width ||
        draggingBoxBounds.y + draggingBoxBounds.height < componentBounds.y ||
        draggingBoxBounds.y > componentBounds.y + componentBounds.height
      );
    });
  }

  private setPositions(node: TreeNodeDto, relocated: number[], level: number) {
    const compRef = this.findComponentRef(node)!;
    this.setComponentPosition(compRef, {
      x: level * this.horizontalGap,
      y: relocated.length * this.verticalGap
    });
    relocated.push(0);
    node.children?.forEach(child => this.setPositions(child, relocated, level + 1));
  }

  private createComponentRefList(node: TreeNodeDto, comps: ComponentRef<MyNode>[]) {
    const comp = this.viewContainer.createComponent(MyNode);
    comp.instance.isOverlapped = false;
    comp.instance.treeNode = node;
    comp.instance.clicked.subscribe(e => this.startDrag(e, comp));
    comps.push(comp);
    node.children?.forEach(child => this.createComponentRefList(child, comps));
  }

  private setComponentPosition(compRef: ComponentRef<MyNode>, position: Point) {
    compRef.location.nativeElement.style.left = `${position.x}px`;
    compRef.location.nativeElement.style.top = `${position.y}px`;
  }

  private collectChildNodes(node: TreeNodeDto, subtree: ComponentRef<MyNode>[]) {
    node.children?.forEach(child => {
      subtree.push(this.findComponentRef(child)!);
      this.collectChildNodes(child, subtree)
    });
  }

  private findComponentRef(node: TreeNodeDto): ComponentRef<MyNode> | undefined {
    return this.nodeComponents().find(compRef => compRef.instance.treeNode === node);
  }
}
