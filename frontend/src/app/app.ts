import {Component, inject, OnInit} from '@angular/core';
import {MyNodeTree} from './components/my-node-tree/my-node-tree';
import {TreeNodeControllerService} from '@ptc-api-services/treeNodeController.service';
import {filter, switchMap} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {EditNodeDialog} from './components/edit-node-dialog/edit-node-dialog';
import {Confirm} from './decorators/confirm-dialog.decorator';
import {ReactiveFormsModule} from '@angular/forms';
import {TreeStore} from './tree.store';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [MyNodeTree, ReactiveFormsModule],
  styleUrl: './app.scss'
})
export class App implements OnInit {
  treeStore = inject(TreeStore);
  treeNodeService = inject(TreeNodeControllerService);

  dialog = inject(MatDialog);

  selectedNode = this.treeStore.selectedNode;


  ngOnInit() {
    this.treeNodeService.getAllNodes().subscribe(nodes => {
      this.treeStore.setNodes(nodes);
    });
  }

  addTreeNode() {
    this.dialog.open(EditNodeDialog, {
      width: '832px'
    }).afterClosed().pipe(
      filter(node => !!node),
      switchMap(node => this.treeNodeService.create({
        ...node,
        parentId: this.selectedNode()!.id!
      }))
    ).subscribe(node => this.treeStore.addNode(node));
  }

  editTreeNode() {
    this.dialog.open(EditNodeDialog, {
      width: '832px',
      data: this.selectedNode()
    }).afterClosed().pipe(
      filter(node => !!node),
      switchMap(node => this.treeNodeService.create(node))
    ).subscribe(node => this.treeStore.updateNode(node));
  }

  @Confirm({ question: "Are you sure you want to delete this node?"})
  deleteTreeNode() {
    this.treeNodeService.deleteNode(this.selectedNode()!.id!).subscribe(() => this.treeStore.deleteNode(this.selectedNode()!));
  }

  search(query: string) {
    this.treeNodeService.search(query).subscribe(nodes => console.log(nodes));
  }
}
