import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserAuthGuardService {
  constructor(
    private router: Router,
    // private accountService: AccountService
) {}

canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    // const user = this.accountService.userValue;
    const user = localStorage.getItem('user');
    if (user) {
        // authorised so return true
        return true;
    }
    // not logged in so redirect to login page with the return url
    // this.router.navigate(['/account/login'], { queryParams: { returnUrl: state.url }});
    this.router.navigate(['/login']);
    return false;
}
}
