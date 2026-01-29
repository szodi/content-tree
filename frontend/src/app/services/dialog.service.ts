import {ComponentType} from '@angular/cdk/portal';
import {inject, Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  private static instance: DialogService | null = null;
  matDialog = inject(MatDialog);

  constructor() {
    DialogService.instance = this;
  }

  public static getInstance() {
    return DialogService.instance;
  }


  openDialog<T>(data: any, component: ComponentType<T>): Observable<boolean> {

    return this.matDialog.open(component, {
      data: data,
      disableClose: true,
    }).afterClosed();

  }
}
