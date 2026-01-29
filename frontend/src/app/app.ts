import {Component, inject, OnInit} from '@angular/core';
import {TreeNodeStore} from './app.store';
import {MyNodeTree} from './components/my-node-tree/my-node-tree';
import {TreeNodeControllerService} from '@ptc-api-services/treeNodeController.service';
import {filter, switchMap} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {EditNodeDialog} from './components/edit-node-dialog/edit-node-dialog';
import {Confirm} from './decorators/confirm-dialog.decorator';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [MyNodeTree],
  providers: [TreeNodeStore],
  styleUrl: './app.scss'
})
export class App implements OnInit {
  treeNodeStore = inject(TreeNodeStore);
  treeNodeService = inject(TreeNodeControllerService);

  dialog = inject(MatDialog);

  rootNode = this.treeNodeStore.rootNode;
  selectedNode = this.treeNodeStore.selectedNode;


  ngOnInit() {
    this.treeNodeService.listTree().subscribe(rootNode => {
      this.treeNodeStore.setTreeNode(rootNode);
      this.treeNodeStore.setSelectedNode(rootNode);
    });
  }

  addTreeNode() {
    this.dialog.open(EditNodeDialog, {
      width: '832px'
    }).afterClosed().pipe(
      filter(node => !!node),
      switchMap(node => this.treeNodeService.create(node))
    ).subscribe(node => this.treeNodeStore.addNode(node, this.selectedNode()!.id!));
  }

  @Confirm({ question: "Are you sure you want to delete this node?"})
  deleteTreeNode() {
    // this.treeNodeService.deleteTreeNode(this.selectedNode()!.id!).subscribe();
  }
}
