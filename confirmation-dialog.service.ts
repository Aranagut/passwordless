import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationDialogComponent } from './confirmation-dialog.component';
import { Injectable, Input } from '@angular/core';

@Injectable()
export class ConfirmationDialogService{
  open: any;

  constructor(private modalService: NgbModal) { }

  public confirm(
    title: string,
    message: string,
    type: string,
    authID: string = '',
    userName: string = '', 
    btnOkText: string = 'Register',
    btnCancelText: string = 'Cancel',
    dialogSize: 'lg' | 'lg' = 'lg'): Promise<boolean> {
      const modalRef = this.modalService.open(ConfirmationDialogComponent, { size: dialogSize,backdrop : 'static',
      keyboard : false});
      modalRef.componentInstance.title = title;
      modalRef.componentInstance.message = message;
      modalRef.componentInstance.authID = authID;
      modalRef.componentInstance.userName = userName;
      modalRef.componentInstance.type = type;
      modalRef.componentInstance.btnOkText = type;
      modalRef.componentInstance.btnCancelText = btnCancelText;

      return modalRef.result;
    }
}
