import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormControlName } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LoginPageService } from 'src/app/services/loginservice/login-page.service';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.css']
})
export class ConfirmationDialogComponent implements OnInit {

  @Input() title: any;
  @Input() message: any;
  @Input() btnOkText: any;
  @Input() btnCancelText: any;
  @Input() public type: string = '';
  @Input() public authID: string = '';
  @Input() public userName: string = '';

  otp: string = '';
  digits: any = [];
  otpObj: any = [];
  enteredOtp: any = '';
  digitCount: number = 6;
  isValidOtp: boolean = true;
  username:any;
  otpInputField:any;
  validateOTPRes:any;
  requestOTPRes:any;
  resendFlag: boolean = false;
  showPassword: boolean = false;
  @ViewChild('innn') input!: ElementRef;
  // deviceName: any;
  // deviceName = new FormControl('');

  constructor(private activeModal: NgbActiveModal, private router: Router, private loginService: LoginPageService) { }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  ngOnInit() {
    this.username = localStorage.getItem('user');
    this.getDigits();
  }

  getDigits() {
    for (let i = 0; i < this.digitCount; i++) {
      this.digits.push(i);
    }
  }

  handleKeyDown(event: any, index: any) {
    const otpContentEle = document.querySelector('.otpContent') || { children: [] };
    console.log(otpContentEle);
    if (event.key !== 'Tab' && event.key !== 'ArrowRight' && event.key !== 'ArrowLeft') {
      event?.preventDefault();
    }
    if (event.key === 'Backspace') {
      const obj = JSON.parse(JSON.stringify(this.otpObj));
      this.otpObj = {};
      this.otpObj = obj;
      delete this.otpObj[index];

      if (index |= 0) {
        this.isValidOtp = true;
        const el: any = (otpContentEle?.children)[index - 1];
        el.focus();
      }
      return;
    }

    if ((new RegExp('^([0-9])$')).test(event.key)) {
      const obj = JSON.parse(JSON.stringify(this.otpObj));
      this.otpObj = {};
      this.otpObj = obj;
      this.otpObj[index] = event.key;

      if (index !== this.digitCount - 1) {
        this.isValidOtp = true;
        const el: any = (otpContentEle.children)[index + 1];
        el.focus();
      }
    }
  }

  public decline() {
    this.activeModal.close(false);
  }

  public accept() {
    this.activeModal.close(true);
  }

  public dismiss() {
    this.activeModal.dismiss();
  }

  public validateOTP() {
    console.log(this.enteredOtp);
    const otpData = this.enteredOtp.trim();
    if (otpData && otpData.length == this.digitCount) {
      this.otp = otpData;
      // otpData.forEach(element => {
      //   this.otp += element;
      // });
      if(this.resendFlag == true){
        if (this.otp && this.otp.length) {
          this.resendFlag = false;
          this.loginService.validateOTP(this.otp, this.requestOTPRes.authId, this.userName).subscribe(
            (response) => {
                this.validateOTPRes = JSON.parse(JSON.stringify(response));
              if (this.validateOTPRes.status == "SUCCESS") {
                this.isValidOtp = true;
                this.activeModal.close(this.otp);
              }
              else {
                this.isValidOtp = false;
                this.enteredOtp = '';
              }
            }, (err) => {
              this.isValidOtp = false;
              this.enteredOtp = '';
            })
        }
      }
      else
      {
      if (this.otp && this.otp.length) {
        this.loginService.validateOTP(this.otp, this.authID, this.userName).subscribe(
          (response) => {
              this.validateOTPRes = JSON.parse(JSON.stringify(response));
            if (this.validateOTPRes.status == "SUCCESS") {
              this.isValidOtp = true;
              this.activeModal.close(this.otp);
            }
            else {
              this.isValidOtp = false;
              this.enteredOtp = '';
            }
          }, (err) => {
            this.isValidOtp = false;
            this.enteredOtp = '';
          })
      }
    }
    } else {
      this.isValidOtp = false;
      this.enteredOtp = '';
    }
  }

  public navigateToLandingpage() {
    this.dismiss();
    this.router.navigate(['/landingpage'])
  }

  public resendOTP() {
    this.isValidOtp = true;
    this.resendFlag = true;
    this.loginService.requestOTP(this.username).subscribe(
      (response) => {
        console.log(response);
        this.requestOTPRes = JSON.parse(JSON.stringify(response));
      });
  }
  public skip() {
    this.dismiss();
    this.router.navigate(['/landingpage'])
  }

  public savePasskey() {
    this.activeModal.close(true);
  }

  public cancel() {
    this.dismiss();
    this.router.navigate(['/login'])
  }

  // public addPasskeyName() {
  //   this.activeModal.close(this.deviceName.value);
  // }
}
