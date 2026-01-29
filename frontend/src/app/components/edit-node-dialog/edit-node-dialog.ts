import {Component, inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {TreeNodeDto} from '@ptc-api-models/treeNodeDto';

@Component({
  selector: 'app-edit-node-dialog',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './edit-node-dialog.html',
  styleUrl: './edit-node-dialog.scss',
})
export class EditNodeDialog {

  data = inject<TreeNodeDto>(MAT_DIALOG_DATA);
  dialogRef = inject(MatDialogRef<EditNodeDialog>);

  fb = inject(FormBuilder);

  form = this.fb.group({
    name: ['', Validators.required],
    content: ['', Validators.required]
  });

  treeNode: TreeNodeDto | undefined;

  constructor() {
    this.treeNode = this.data;
    if (this.treeNode) {
      this.form.patchValue({name: this.treeNode.name, content: this.treeNode.content});
    }
  }

  submit() {
    this.treeNode = {
      ...this.treeNode,
      name: this.form.value.name!,
      content: this.form.value.content!
    }
    this.dialogRef.close(this.treeNode);
  }

  close() {
    this.dialogRef.close(null);
  }
}
