import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { PublicKeyCredential } from "@ownid/webauthn";
import { LoginPageService } from '../../services/loginservice/login-page.service';
import { ConfirmationDialogService } from '../confirmation-dialog/confirmation-dialog.service';
import { Router } from '@angular/router';
import { EndPointConstants } from '../EndPointConstants';

declare const signalData: any;
declare const userAgentInfo: any;

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {
  loginForm!: FormGroup;
  showPassword: boolean = false;
  isValidUser: boolean = true;
  responseData: any;
  attestationVal: any;
  otp: string = "";
  suggestions = false;
  items = ['abc@gmail.com', 'xyz@gmail.com'];
  invalidSubmission = false;
  requestOTPResp: any;

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
    const passwordInput: any = document.getElementById('password');
    passwordInput.type = this.showPassword ? 'text' : 'password';
  }
  public authAbortController: any = window.PublicKeyCredential ? new AbortController() : null;
  public authAbortSignal: any = window.PublicKeyCredential ? this.authAbortController.signal : null;
  
  constructor(private confirmationDialogService: ConfirmationDialogService, private loginService: LoginPageService, private router: Router, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.loginForm = new FormGroup({
      userName: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required]),
    });
  }

  loginwithPasskey() {
    console.log("userName value:", this.loginForm.controls['userName'].value);
    console.log("Form validity:", this.loginForm.invalid);
    if (this.loginForm.value.userName == "") {
      this.deviceAuthNoUsername();
      console.log("Logging with Passkeys with No Username");
    }
    else {
      localStorage.setItem('user', this.loginForm.value.userName);
      this.deviceAuthentication(this.loginForm.value.userName);
      console.log("Logging with Passkeys with Username");
    }
  }

  // convenience getter for easy access to form fields
  get f() { return this.loginForm.controls; }
  submitted = false;
  onSubmit() {
    this.isValidUser = true;
    this.submitted = true;
    if (this.loginForm.invalid) {
      return;
    }
    
    localStorage.setItem('user', this.loginForm.value.userName);
    this.loginService.login(this.loginForm.value.userName, this.loginForm.value.password, "", userAgentInfo, signalData).subscribe(
      (response) => {
        this.responseData = JSON.parse(JSON.stringify(response));
        if (this.responseData.status == "SUCCESS") {
          if (this.responseData.riskLevel == "HIGH" || this.responseData.riskLevel == "MEDIUM") {
            this.mfaRequest(this.loginForm.value.userName)
            this.isValidUser = true;
          }
          else {
            this.isFIDORegisterFlow(this.loginForm.value.userName);
           }
        }
        else {
          this.isValidUser = false;
        }
      },
      (error) => {
        console.log(error);
        this.confirmationDialogService.confirm('', 'Please try after sometime', 'errorMessageDialogue').then((confirmed) => { })
      }
    )
  }

  mfaRequest(username: string) {
    this.loginService.requestOTP(username).subscribe(
      (response) => {
        let requestOTPRes = JSON.parse(JSON.stringify(response));
        if (requestOTPRes.status == "SUCCESS") {
          this.confirmationDialogService.confirm('', requestOTPRes.message, 'OTPValidate', requestOTPRes.authId, this.loginForm.value.userName)
            .then((confirmed: any) => {
              if (confirmed && confirmed.length) {
                this.isValidUser = true;
                localStorage.setItem('user', this.loginForm.value.userName);
                this.loginService.isFIDO2Registered(this.loginForm.value.userName, EndPointConstants.rp).subscribe(
                  (response) => {
                    this.router.navigate(['/landingpage']);
                    this.responseData = JSON.parse(JSON.stringify(response));
                    if (!this.responseData) {
                      this.registerdevice(this.loginForm.value.userName);
                    }
                  },
                  (error) => {
                    this.router.navigate(['/landingpage']);
                    this.registerdevice(this.loginForm.value.userName);
                    console.log(error);
                  })
              } else {
                this.isValidUser = false;
              }
            },
              (err: any) => {
                console.log(err);
              });
        }
      },
      (error) => {
        console.log(error);
        this.confirmationDialogService.confirm('', 'Request OTP failed, Please try again.', 'errorMessageDialogue').then((confirmed) => {
         
        })
      }
    )
  }

  isFIDORegisterFlow(username: string) {
    this.loginService.isFIDO2Registered(username, EndPointConstants.rp).subscribe(
      (response) => {
        this.router.navigate(['/landingpage']);
        this.responseData = JSON.parse(JSON.stringify(response));
        if (!this.responseData) {
          this.registerdevice(username);
        }
      },
      (error) => {
        console.log(error);
          this.confirmationDialogService.confirm('', 'Failed to initiate device registration. , Please try again.', 'errorMessageDialogue').then((confirmed) => { })

      }
    )
  }

  registerdevice(username: string) {
    this.confirmationDialogService.confirm('', '', 'savePasskey')
      .then((confirmed: any) => {
        if (confirmed) {
        this.otp = "";
        this,this.loginService.requestOTP(username).subscribe(
          (response) => {
            this.requestOTPResp = JSON.parse(JSON.stringify(response));
            if(this.requestOTPResp.status == "SUCCESS") {
              this.confirmationDialogService.confirm('', this.requestOTPResp.message, 'OTPValidate', this.requestOTPResp.authId, username)
              .then((confirmed: any) => {console.log('Device Registration started:', confirmed);
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
              )})
            }
          }
        )
        }
      })
      .catch(() => console.log('User dismissed the dialog'));
  }

  // registerdevice(username: string) {
  //   this.confirmationDialogService.confirm('', '', 'savePasskey')
  //   .then((confirmed: any) => {
  //   // if (confirmed){
  //     // this.confirmationDialogService.confirm('', '', 'deviceName').then((devicename: any) => {
  //     //   if (devicename) {
  //     //   console.log('Device name:', devicename);
  //         console.log('Device Registration started:', confirmed);
  //         this.loginService.register("localhost", "localhost", username).subscribe(
  //           (response) => {
  //             this.responseData = JSON.parse(JSON.stringify(response));
  //             if (this.responseData.status == "ACTIVATION_REQUIRED") {
  //               this.attestationObtainer(this.responseData.publicKeyCredentialCreationOptions).then((registrationInfo: any) => {
  //                 let deviceAuthObj = {
  //                   id: registrationInfo.id,
  //                   type: registrationInfo.type,
  //                   rawId: registrationInfo.rawId,
  //                   response: registrationInfo.response,
  //                 };
  //                 this.attestationVal = JSON.stringify(deviceAuthObj);
  //                 if (this.attestationVal != null) {
  //                   this.loginService.activate(location.origin, this.attestationVal, this.responseData.authId, "FIDO2", username).subscribe(
  //                     (response) => {
  //                       console.log('Device Activation started');
  //                       this.responseData = JSON.parse(JSON.stringify(response));
  //                       if (this.responseData.status == "ACTIVE") {
  //                         this.confirmationDialogService.confirm('Registration Successful!', 'Click OK to continue to landing page', 'SuccessfulRegistartion')
  //                           .then((confirmed) => {
  //                             if (confirmed) {
  //                               this.router.navigate(['/landingpage']);
  //                             }
  //                           })
  //                       }
  //                     },
  //                     (error) => {
  //                       console.log(error);
  //                     }
  //                   )
  //                 }
  //               })
  //             }
  //           },
  //           (error) => {
  //             console.log(error);
  //           }
  //         )
  //       }
  //     // });
  //   // }
  //     // })
  //     .catch(() => console.log('User dismissed the dialog'));
  // }

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

  deviceAuthentication(username: string) {
    this.loginService.initializedeviceauthentication(username, EndPointConstants.rp).subscribe((data: any) => {
      this.authenticateDevice(data.publicKeyCredentialRequestOptions).then((assertion) => {
        this.loginService.authenticateFIDOWithAssertion(location.origin, assertion, data.authId).subscribe((res: any) => {
          console.log(res);
          this.router.navigate(['/landingpage'])
        }) 
        // Device authentication failed.
      })
    }, (err) => {
      console.log("in error block.........." + err);
      this.confirmationDialogService.confirm('', 'Initiate authentication failed.', 'errorMessageDialogue').then((confirmed) => { })
    })
  }

  deviceAuthNoUsername() {
    this.loginService.initializedeviceauthNoUsername("", EndPointConstants.rp).subscribe((data: any) => {
      this.authenticateDevice(data.publicKeyCredentialRequestOptions).then((assertion) => {
        this.loginService.authenticateFIDOWithAssertion(location.origin, assertion, data.authId).subscribe(
          (res: any) => {
          console.log(res);
            let responseJSON = JSON.parse(JSON.stringify(res));
            if (responseJSON.status == "SUCCESS") {
              localStorage.setItem('user', responseJSON.username);
              this.router.navigate(['/landingpage']);
            }
        },
        (error) => {
          console.log(error);
          this.confirmationDialogService.confirm('', 'Failed to initiate device registration. , Please try again.', 'errorMessageDialogue').then((confirmed) => { })
        }
        ) 
        // Device authentication failed.
      })
    }, (err) => {
      console.log("in error block.........." + err);
      this.confirmationDialogService.confirm('', 'Initiate authentication failed.', 'errorMessageDialogue').then((confirmed) => { })
    })
  }

  public authenticateDevice(publicKeyCredentialRequestOptions: PublicKeyCredential) {
    return new Promise((resolve, reject) => {
      let options: any = JSON.parse(publicKeyCredentialRequestOptions);
      let publicKeyCredential: any = {};
      publicKeyCredential.challenge = new Uint8Array(options.challenge);
      if ('allowCredentials' in options) {
        publicKeyCredential.allowCredentials = this.credentialListConversion(options.allowCredentials);
      }
      if ('rpId' in options) {
        publicKeyCredential.rpId = options.rpId;
      }
      if ('timeout' in options) {
        publicKeyCredential.timeout = options.timeout;
      }
      if ('userVerification' in options) {
        publicKeyCredential.userVerification = options.userVerification;
      }
      navigator.credentials.get({ "publicKey": publicKeyCredential })
        .then((assertion: any) => {
          // Send new credential info to server for verification and registration.
          let publicKeyCredential: any = {};
          if ('id' in assertion) {
            publicKeyCredential.id = assertion.id;
          }
          if ('rawId' in assertion) {
            publicKeyCredential.rawId = this.toBase64Str(assertion.rawId);
          }
          if ('type' in assertion) {
            publicKeyCredential.type = assertion.type;
          }

          let response: any = {};
          response.clientDataJSON = this.toBase64Str(assertion.response.clientDataJSON);
          response.authenticatorData = this.toBase64Str(assertion.response.authenticatorData);
          response.signature = this.toBase64Str(assertion.response.signature);
          response.userHandle = this.toBase64Str(assertion.response.userHandle);
          publicKeyCredential.response = response;

          let obj = {
            id: publicKeyCredential.id,
            rawId: publicKeyCredential.rawId,
            type: publicKeyCredential.type,
            response: publicKeyCredential.response
          };
          let assertionVal = JSON.stringify(obj);
          resolve(assertionVal);

        }).catch(function (err) {
          // No acceptable authenticator or user refused consent. Handle appropriately.
          reject(Error(err.name));
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

  showSuggestions() {
    this.suggestions = true;
  }

  disableSuggestions() {
    this.suggestions = false;
  }

  registerWithKey(item: any) {
    console.log(item);
    this.suggestions = false;
  }

  loginWithOtherAccount() {
    console.log('loginWithOtherAccount');
    this.suggestions = false;
  }

  passkeyFromOtherDevice() {
    console.log('passkeyFromOtherDevice');
    this.suggestions = false;
  }

}





