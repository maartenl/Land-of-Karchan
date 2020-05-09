import { Injectable, Injector } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import * as reginald from '../assets/characters/reginald.json';

/**
 * Useful for mocking the http rest calls in a testing environment.
 */
@Injectable()
export class MockHttpInterceptor implements HttpInterceptor {
    private urlMapper = new Map();

    constructor(private injector: Injector) {
        this.urlMapper.set('/assets/characters.json/Reginald', reginald);
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (this.urlMapper.has(request.url)) {
            console.log('Hit ' + request.url);
            return of(new HttpResponse({ status: 200, body: ((this.urlMapper.get(request.url)) as any).default }));
        } else {
            console.log('No hit for ' + request.url);
        }
        return next.handle(request);
    }
}