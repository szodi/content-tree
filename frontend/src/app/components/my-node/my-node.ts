import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TreeNodeDto} from '@ptc-api-models/treeNodeDto';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-my-node',
  imports: [NgClass],
  templateUrl: './my-node.html',
  styleUrl: './my-node.scss',
})
export class MyNode {

  @Input() isSelected= false;
  @Input() isOverlapped= false;
  @Input() treeNode: TreeNodeDto | undefined;

  @Output() clicked = new EventEmitter<MouseEvent>();

  onClick(event: MouseEvent) {
    this.clicked.emit(event);
  }
}
