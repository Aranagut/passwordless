import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';

import { finalize } from 'rxjs/operators';
import { LoaderService } from '../loader.service';


@Injectable()
export class authInterceptor implements HttpInterceptor {
    private totalRequests = 0;

  constructor(
    private loadingService: LoaderService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    this.totalRequests++;
    const begin = performance.now();
    this.loadingService.setLoading(true);
    return next.handle(request).pipe(
      finalize(() => {
        this.totalRequests--;
        if (this.totalRequests == 0) {
          this.loadingService.setLoading(false);
        }
        this.logRequestTime(begin, request.url, request.method);
      })
    );
  }

  private logRequestTime(startTime: number, url: string, method: string) {
    const requestDuration = `${performance.now() - startTime}`;
    console.log(`HTTP ${method} ${url} - ${requestDuration} milliseconds`);
  }
}