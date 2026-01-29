import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgClass} from '@angular/common';
import {TreeNode} from '@ptc-api-models/treeNode';

@Component({
  selector: 'app-my-node',
  imports: [NgClass],
  templateUrl: './my-node.html',
  styleUrl: './my-node.scss',
})
export class MyNode {

  @Input() isHighlighted= false;
  @Input() isHalfHighlighted= false;
  @Input() isSelected= false;
  @Input() isOverlapped= false;
  @Input() dragging= false;
  @Input() treeNode: TreeNode | undefined;

  @Output() clicked = new EventEmitter<MouseEvent>();

  onClick(event: MouseEvent) {
    this.clicked.emit(event);
  }
}
