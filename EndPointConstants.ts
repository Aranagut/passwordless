export class EndPointConstants {

    //Server specific - Do not comment out while checkin
      public static BASE_URL = location.origin+"/ciam-fido2";
    //Taking domain name for Rp from location.origin
      public static rp = location.origin.split('//')[1].split('/')[0];

    // For Local  
    // public static BASE_URL = "http://localhost:9090/ciam-fido2";
    //public static BASE_URL = "http://localhost:8080";
    // Taking domain name for Rp from location.origin
    //public static rp = location.origin.split('//')[1].split(':')[0];

}