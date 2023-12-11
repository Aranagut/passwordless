import { Component, OnInit } from '@angular/core';
import { ConfirmationDialogService } from '../confirmation-dialog/confirmation-dialog.service';
import { LoginPageService } from 'src/app/services/loginservice/login-page.service';
import { Router } from '@angular/router';
import { PublicKeyCredential } from "@ownid/webauthn";
import { EndPointConstants } from '../EndPointConstants';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent implements OnInit {

  public authAbortController: any = window.PublicKeyCredential ? new AbortController() : null;
  public authAbortSignal: any = window.PublicKeyCredential ? this.authAbortController.signal : null;
  otp: string = "";
  responseData: any;
  username: any;
  attestationVal: any;
  requestOTPRes: any;
  validateOTPRes: any;
  activeModal: any;
  type: string = '';
  constructor(private confirmationDialogService: ConfirmationDialogService, private loginService: LoginPageService, private router: Router) { }

  ngOnInit() {
    this.username = localStorage.getItem('user');
  }

  register() {
    this.otp = "";
    this.loginService.requestOTP(this.username).subscribe(
      (response) => {
        this.requestOTPRes = JSON.parse(JSON.stringify(response));
        if (this.requestOTPRes.status == "SUCCESS") {
          this.confirmationDialogService.confirm('', this.requestOTPRes.message, 'OTPValidate', this.requestOTPRes.authId, this.username)
            .then((confirmed: any) => {
              this.registerdevice(this.username);
            },
              (err: any) => {
                console.log(err);
                //  this.confirmationDialogService.confirm('', 'Please try after sometime', 'errorMessageDialogue').then((confirmed) => { })   
              });
        }
      },
      (error) => {
        console.log(error);
        //  this.confirmationDialogService.confirm('', 'Please try after sometime', 'errorMessageDialogue').then((confirmed) => { })   
      }
    )
  }

  registerdevice(username: string) {

    this.loginService.register(EndPointConstants.rp, EndPointConstants.rp, username).subscribe(
      (response) => {
        this.responseData = JSON.parse(JSON.stringify(response));
        if (this.responseData.status == "ACTIVATION_REQUIRED") {
          this.attestationObtainer(this.responseData.publicKeyCredentialCreationOptions).then((registrationInfo: any) => {
            let deviceAuthObj = {
              id: registrationInfo.id,
              type: registrationInfo.type,
              rawId: registrationInfo.rawId,
              response: registrationInfo.response,
            };
            this.attestationVal = JSON.stringify(deviceAuthObj);
            if (this.attestationVal != null) {
              this.loginService.activate(location.origin, this.attestationVal, this.responseData.authId, "FIDO2", username).subscribe(
                (response) => {
                  console.log('Device Activation started');
                  this.responseData = JSON.parse(JSON.stringify(response));
                  if (this.responseData.status == "ACTIVE") {
                    this.confirmationDialogService.confirm('Registration Successful!', 'Click OK to continue to landing page', 'SuccessfulRegistartion')
                      .then((confirmed) => {
                        if (confirmed) {
                          this.router.navigate(['/landingpage']);
                        }
                      })
                  }
                },
                (error) => {
                  console.log(error);
                  this.confirmationDialogService.confirm('', 'Device activation failed. Please try again later.', 'errorMessageDialogue').then((confirmed) => { })
                }
              )
            }
          })
        }
      },
      (error) => {
        console.log(error);
        this.confirmationDialogService.confirm('', 'Device registration failed. Please try again later.', 'errorMessageDialogue').then((confirmed) => { })
      }
    )
  }



  attestationObtainer(publicKeyCredentialCreationOptions: PublicKeyCredential) {
    return new Promise((resolve, reject) => {
      let options: any = JSON.parse(publicKeyCredentialCreationOptions);
      let publicKeyCredential: any = {};
      publicKeyCredential.rp = options.rp;
      publicKeyCredential.user = options.user;
      publicKeyCredential.user.id = new Uint8Array(options.user.id);
      publicKeyCredential.challenge = new Uint8Array(options.challenge);
      publicKeyCredential.pubKeyCredParams = options.pubKeyCredParams;
      // Optional parameters
      if ('timeout' in options) {
        publicKeyCredential.timeout = options.timeout;
      }
      if ('excludeCredentials' in options) {
        publicKeyCredential.excludeCredentials = this.credentialListConversion(options.excludeCredentials);
      }
      if ('authenticatorSelection' in options) {
        publicKeyCredential.authenticatorSelection = options.authenticatorSelection;
      }
      if ('attestation' in options) {
        publicKeyCredential.attestation = options.attestation;
      }
      if ('extensions' in options) {
        publicKeyCredential.extensions = options.extensions;
      }
      navigator.credentials.create({ "publicKey": publicKeyCredential, "signal": this.authAbortSignal })
        .then((newCredentialInfo: any) => {
          // let publicKeyCredential: any = {};
          if ('id' in newCredentialInfo) {
            publicKeyCredential.id = newCredentialInfo.id;
          }
          if ('type' in newCredentialInfo) {
            publicKeyCredential.type = newCredentialInfo.type;
          }
          if ('rawId' in newCredentialInfo) {
            publicKeyCredential.rawId = this.toBase64Str(newCredentialInfo.rawId);
          }
          if (!newCredentialInfo.response) {
            throw "Missing 'response' attribute in credential response";
          }
          let response: any = {};
          response.clientDataJSON = this.toBase64Str(newCredentialInfo.response.clientDataJSON);
          response.attestationObject = this.toBase64Str(newCredentialInfo.response.attestationObject);
          publicKeyCredential.response = response;
          resolve(publicKeyCredential);
        });
    });
  }

  credentialListConversion(list: any) {
    let credList = [];
    for (let i = 0; i < list.length; i++) {
      let cred: any = {
        type: list[i].type,
        id: new Uint8Array(list[i].id)
      };
      if (list[i].transports) {
        cred.transports = list[i].transports;
      }
      credList.push(cred);
    }
    return credList;
  }

  toBase64Str(bin: any) {
    let ascii: any = new Uint8Array(bin);
    return btoa(String.fromCharCode.apply(null, ascii));
  }

  public navigateToLandingpage() {
    this.router.navigate(['/homepage']);
  }

  logOut() {
      this.confirmationDialogService.confirm('', 'Are you sure you want to logout?', 'logoutDialouge').then((confirmed) => {
        if (confirmed) {
          localStorage.removeItem('user');
          localStorage.clear();
          this.router.navigate(['/login']);
        }
      },
        (onReject) => {
          if (onReject) {
            console.log(onReject);
          }
        })
    /* localStorage.removeItem('user');
    localStorage.clear();
    this.router.navigate(['/login']) */;
  }

  public unpairDevice() {
    this.confirmationDialogService.confirm('', '', 'unpairDevice').then((confirmed) => {
      if (confirmed) {
        this.loginService.unpairdevice(this.username).subscribe(
          (response) => {
            this.responseData = JSON.parse(JSON.stringify(response));
            if (this.responseData.status == "SUCCESS") {
              this.confirmationDialogService.confirm('Successfully Unpaired', 'Click OK to continue to landing page', 'SuccessFullUnpaired')
                .then((confirmed) => {
                  if (confirmed) {
                    this.router.navigate(['/landingpage']);
                  }
                })
            }
          },
          (error) => {
            console.log(error);
            this.confirmationDialogService.confirm('', 'Device Unpairing Failed, Please try again', 'errorMessageDialogue').then((confirmed) => {
              if (confirmed) {
                this.router.navigate(['/landingpage']);
              }
            })
          }
        )
      }
    })
  }

}
