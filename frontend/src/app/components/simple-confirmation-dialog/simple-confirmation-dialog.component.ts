import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-simple-confirmation-dialog',
  templateUrl: './simple-confirmation-dialog.component.html',
  styleUrls: ['./simple-confirmation-dialog.component.scss'],
})
export class SimpleConfirmationDialogComponent {
  constructor(public dialogRef: MatDialogRef<SimpleConfirmationDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {}

  close(result: Boolean): void {
    this.dialogRef.close(result);
  }
}
