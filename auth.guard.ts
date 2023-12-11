import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

// import { AccountService } from '../_services';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
    constructor(
        private router: Router,
        // private accountService: AccountService
    ) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        // const user = this.accountService.userValue;
        const user = localStorage.getItem('user');
        if (user) {
            // authorised so return true
            // console.log(route.url);
            // if (route.url=='login') {
            //     this.router.navigate(['homepage']);
            //     return false;
            // }
            return true;
        }
        // not logged in so redirect to login page with the return url
        // this.router.navigate(['/account/login'], { queryParams: { returnUrl: state.url }});
        this.router.navigate(['/login']);
        return false;
    }
}