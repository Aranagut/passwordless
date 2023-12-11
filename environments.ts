export const environment = {
    PingOne : {
    isFIDO: false,
    authUrl: "https://auth.pingone.com",
    FIDO_authUrl: "https://ciam-mfa-api-dev.1dc.com",
    envId: "6c173a64-a0b2-4779-b38a-fa55c510fe09",
    baseUrl:"https://localhost:4200/api/",
    createDeviceUrl: "https://api.pingone.com/v1",
    userId: "d862b308-b410-4ccd-9392-5fe9686b0a34",
    appId: "9212de9f-d1cb-4b8f-9b53-818a6818e893",
    appSecretId: "67q7NicW6t9bQYP1raiSECwFgS6eWCb7IEu9HUVIZOa9EHzm~fA37nudLIe-qMTK",
    },
    FIDO2: {
        isFIDO: true,
        authUrl: "https://ciam-mfa-api-dev.1dc.com",
        username: "rangarajan.kannan",
        baseUrl: "http://localhost:8081",
        appId: "Sg5qx4cvh",
        appSecretId: "}oIJt3iUEO",
    },
    production:false,
}
