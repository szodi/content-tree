import {Component, inject} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  selector: 'app-edit-node-dialog',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './edit-node-dialog.html',
  styleUrl: './edit-node-dialog.scss',
})
export class EditNodeDialog {

  // data = inject<Observable<DocumentDto[]>>(MAT_DIALOG_DATA);
  dialogRef = inject(MatDialogRef<EditNodeDialog>);

  fb = inject(FormBuilder);

  // templates: DocumentDto[];

  form = this.fb.group({
    name: ['', Validators.required],
    content: ['', Validators.required]
  });

  constructor() {
    // this.data.subscribe(templates => {
    //   this.templates = templates;
    //   this.form.patchValue(templates[0])
    // });
  }

  submit() {
    this.dialogRef.close(this.form.value);
  }

  close() {
    this.dialogRef.close(null);
  }
}
