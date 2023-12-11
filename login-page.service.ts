import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EndPointConstants } from 'src/app/components/EndPointConstants';


@Injectable({
  providedIn: 'root'
})
export class LoginPageService {


  public authAbortController: any = window.PublicKeyCredential ? new AbortController() : null;
  public authAbortSignal: any = window.PublicKeyCredential ? this.authAbortController.signal : null;

  // options1: any = { observe: 'response', response: 'text' }
  // // private userSubject: BehaviorSubject<any>;
  // // public user: Observable<any>;
  // public envConfig: any;
  // registeredUsers: any = ['Praveen'];

  constructor(private http: HttpClient) {
    // this.envConfig = environment.PingOne;
    // this.envConfig = environment.FIDO2;
    // this.userSubject = new BehaviorSubject(JSON.parse(localStorage.getItem('user')!));
    // this.user = this.userSubject.asObservable();
  }

  // public authenticateuser(user: any) {
  //   if (user.email != '' && user.password != '') {
  //     return true
  //   }
  //   return false
  // }

  login(username: string, password: string, ip: string, userAgent: string, sdkdata: string): Observable<any> {
    return this.http.post<any>(
      EndPointConstants.BASE_URL + "/authenticate",
      { username, password, ip, userAgent, sdkdata }
    );
  }

  isFIDO2Registered(username: string, rp: string): Observable<any> {
    return this.http.post<any>(
      EndPointConstants.BASE_URL + "/isFIDO2Registered",
      { username, rp }
    );
  }

  register(rpID: string, rpName: string, username: string): Observable<any> {
    return this.http.post<any>(
      EndPointConstants.BASE_URL + "/registerFIDO",
      { rpID, rpName, username }
    );
  }

  activate(origin: string, attestation: string, authId: string, deviceType: string, username: string): Observable<any> {
    return this.http.post<any>(
      EndPointConstants.BASE_URL + "/activateFIDO",
      { origin, attestation, authId, deviceType, username }
    );
  }

  requestOTP(username: string): Observable<any> {
    return this.http.post<any>(
      EndPointConstants.BASE_URL + "/requestOTP",
      { username }
    );
  }

  validateOTP(otp: string, authID: string, user_name: string): Observable<any> {
    return this.http.post<any>(
      EndPointConstants.BASE_URL + "/validateOTP",
      { otp, authID, user_name }
    )
  }

  public initializedeviceauthentication(userName: string, rpID: string) {
    return this.http.post<any>(
      EndPointConstants.BASE_URL + "/initiateAuthFIDO",
      { userName, rpID }
    )
  }

  public initializedeviceauthNoUsername(userName: string, rpID: string) {
    return this.http.post<any>(
      EndPointConstants.BASE_URL + "/initiateAuthFIDONoUsername",
      { userName, rpID }
    )
  }


  // public assertion(assertion: any, deviceId: any) {
  //   const httpOptions =
  //   {
  //     headers: new HttpHeaders({
  //       'content-type': 'application/vnd.pingidentity.assertion.check+json',
  //       'Authorization': 'Bearer ' + localStorage.getItem('token')
  //     })
  //   };
  //   let body = {
  //     "origin": "https://localhost:4200",
  //     "assertion": assertion,
  //     "compatibility": "FULL"
  //   }
  //   return this.http.post(`${this.envConfig.authUrl}/${this.envConfig.envId}/deviceAuthentications/${deviceId}`, body, httpOptions);
  // }

  authenticateFIDOWithAssertion(origin: string, assertion: any, authID: any) {
    return this.http.post<any>(
      EndPointConstants.BASE_URL + "/authenticateFIDO",
      { origin, assertion, authID }
    )
  }

  unpairdevice(username: string) { 
    return this.http.post<any>(EndPointConstants.BASE_URL + "/unpairFIDO2Devices",
    { username }
    )
  }

  // public getregistereddevicestatus(userName: any) {
  //   if (this.registeredUsers.includes(userName)) {
  //     return true;
  //   }
  //   return false;
  // }

  // public getToken() {
  //   const httpOptions = 
  //   { headers: new HttpHeaders({
  //      'Content-Type': 'application/x-www-form-urlencoded',
  //      'Authorization': 'Basic '+ btoa(`${this.envConfig.appId}:${this.envConfig.appSecretId}`)
  //     })
  //   };

  //   let body = new URLSearchParams(); 
  //   body.set('grant_type', 'client_credentials'); 
  //   // body.set('client_id', 'Sg5qx4cvh');
  //   // body.set('client_secret', '}oIJt3iUEO');

  //   if (this.envConfig.isFIDO) {
  //     return this.http.post(`${this.envConfig.authUrl}/ciam-mfa/v2/get/token`, body, httpOptions);
  //   } else{
  //     return this.http.post(`${this.envConfig.authUrl}/${this.envConfig.envId}/as/token`, body, httpOptions);
  //   }
  // }

  // public createUserDevice(accessToken: any, type: string) {
  //   const httpOptions = 
  //   { headers: new HttpHeaders({
  //      'Content-Type': 'application/json', 
  //      'Authorization': type +' '+ accessToken
  //     })
  //   };

  //   let body = {
  //       "type": "FIDO2",
  //       "rp": {
  //           "id": "localhost",
  //           "name": "PingOne"
  //       }
  //   };
  //   return this.http.post(`${this.envConfig.createDeviceUrl}/environments/${this.envConfig.envId}/users/${this.envConfig.userId}/devices`, body, httpOptions);
  // }

  // public activateUserdevice(accessToken:any, deviceId: string, attestationObj: any){
  //   const httpOptions = 
  //   { headers: new HttpHeaders({
  //      'Content-Type': 'application/vnd.pingidentity.device.activate+json', 
  //      'Authorization': 'Bearer '+ accessToken
  //     }) 
  //   };
  //   //let body = {
  //     //"origin": "https://localhost:4200",
  //     //"attestation": "{\"id\":\"ARacmDOuRE7DJV6L7w\",\"type\":\"public-key\",\"rawId\":\"ARacmDOuRE7DJV6L7w=\",\"response\":{\"clientDataJSON\":\"eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRYWxzZX0=\",\"attestationObject\":\"o2NmbXRmcGFja2VkZ2F0dFFO29h8n6WKBn6tHCQ=\"},\"clientExtensionResults\":{}}"
  // //}
  //   let body = {
  //     "origin": "https://localhost:4200",
  //     "attestation": attestationObj
  //   }
  //  return this.http.post(`${this.envConfig.createDeviceUrl}/environments/${this.envConfig.envId}/users/${this.envConfig.userId}/devices/${deviceId}`, body, httpOptions);
  // }

  // public initializedeviceauthentication(){
  //   const httpOptions = 
  //   { headers: new HttpHeaders({
  //      'Authorization': 'Bearer '+ localStorage.getItem('token')
  //     })
  //   };
  //   let body = {
  //     user: {'id': this.envConfig.userId}
  //   }
  // return this.http.post(`${this.envConfig.authUrl}/${this.envConfig.envId}/deviceAuthentications`, body, httpOptions);
  // }

  // public deviceauthentication() {
  //   const httpOptions = 
  //   { headers: new HttpHeaders({
  //      'Authorization': 'Bearer '+ localStorage.getItem('token')
  //     })
  //   };
  //   let body = {
  //     rp: {'id': "localhost"}
  //   }
  // return this.http.post(`${this.envConfig.authUrl}/${this.envConfig.envId}/deviceAuthentications`, body, httpOptions);
  // }

  // public assertion(assertion:any,deviceId:any) {
  //   const httpOptions = 
  //   { headers: new HttpHeaders({
  //     'content-type': 'application/vnd.pingidentity.assertion.check+json',
  //      'Authorization': 'Bearer '+ localStorage.getItem('token')
  //     })
  //   };
  //   let body = {
  //     "origin": "https://localhost:4200",
  //     "assertion": assertion,
  //     "compatibility" : "FULL"
  //   }
  // return this.http.post(`${this.envConfig.authUrl}/${this.envConfig.envId}/deviceAuthentications/${deviceId}`, body, httpOptions);
  // }

  // // public get userValue() {
  // //   return this.userSubject.value;
  // // }

  // getAll(path: string, options?: any): Observable<any> {
  //   return this.http.get(`${this.envConfig.baseUrl}${path}`, options);
  // }

  // putAll(path: string, data: any, options?: any): Observable<any> {
  //   return this.http.put(`${this.envConfig.baseUrl}${path}`, data, options);
  // }

  // postAll(path: string, data: any, options?: any): Observable<any> {
  //   return this.http.post(`${this.envConfig.baseUrl}${path}`, data, options);

  //   localStorage.setItem('user', JSON.stringify(data));
  //   // this.userSubject.next(data);
  //   // .pipe(map(user => {
  //   //     return user;
  //   //   }));
  // }


  // delete(path: string): Observable<any> {
  //   return this.http.delete<any>(`${this.envConfig.baseUrl}${path}`);
  // }
}
