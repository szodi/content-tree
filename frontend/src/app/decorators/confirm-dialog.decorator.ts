import {ConfirmDialogData} from '../../main';
import {DialogService} from '../services/dialog.service';
import {
  SimpleConfirmationDialogComponent
} from '../components/simple-confirmation-dialog/simple-confirmation-dialog.component';

const defaultConfirmData = {
  question: "Are you sure you want to perform this action?"
}


export function Confirm (confirmData : ConfirmDialogData = defaultConfirmData) {

  return function (target: Object, propertyKey: string, descriptor: PropertyDescriptor) {
    const originalMethod = descriptor.value;

    descriptor.value = async function (...args: any) {
      DialogService.getInstance()?.openDialog(confirmData, SimpleConfirmationDialogComponent).subscribe((validation: any) => {
        if (validation){
          originalMethod.apply(this, args);
        }
      });
    };

    return descriptor;
  };

}
