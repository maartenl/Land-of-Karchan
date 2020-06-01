import { Injectable, Injector } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { Board, BoardMessage } from './boards/board.model';

export class RestService {
    urlRegExp: string;
    result: Array<any>;
    creator;

    constructor(urlRegExp: string, creator: any, initialArray?: Array<any>) {
        this.urlRegExp = urlRegExp;
        this.creator = creator;
        if (initialArray === undefined) {
            this.result = new Array();
        } else {
            this.result = initialArray.map(x => creator(x));
        }
    }

    hasPartial(url: string): boolean {
        return url !== this.urlRegExp && url.search(this.urlRegExp) === 0;
    }

    getPartial(url: string) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    getById(url: string) {
        return this.result.find(x => x.getIdentifier() == this.getPartial(url));
    }

    updateById(url: string, body) {
        this.result = this.result.map(x => {
            if (x.getIdentifier() == this.getPartial(url)) {
                return this.creator(body);
            } else {
                return x;
            }
        });
    }

    deleteById(url: string) {
        this.result = this.result.filter(x => x.getIdentifier() != this.getPartial(url));
    }

    add(body) {
        body.creation = new Date();
        body.owner = 'Karn';
        this.result.push(this.creator(body));
    }
}

export class RestServer {
    private restservices: Array<RestService> = new Array();

    constructor() {
        this.restservices.push(new RestService('/karchangame/resources/administration/boards/1/messages',
            (x: any) => new BoardMessage(x), [{
                id: 1,
                boardid: 1,
                name: 'Midevia',
                posttime: '2020-12-02 13:14:15',
                message: 'This is my message.',
                removed: false
            }, {
                id: 2,
                boardid: 1,
                name: 'Karn',
                posttime: '2020-10-02 13:14:15',
                message: 'This is a curse message with much cursing.',
                removed: true
            }]));
        this.restservices.push(new RestService('/karchangame/resources/administration/boards', (x: any) => new Board(x)));
    }

    getRestService(url: string): RestService {
        return this.restservices.find(x => url.search(x.urlRegExp) !== -1);
    }

}

/**
 * Useful for mocking the http rest calls in a testing environment.
 */
@Injectable()
export class MockHttpInterceptor implements HttpInterceptor {
    private restServer: RestServer = new RestServer();

    constructor(private injector: Injector) {
        // this.urlMapper.set('/assets/characters.json/Reginald', reginald);
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const restService = this.restServer.getRestService(request.url);
        if (restService !== undefined) {
            if (request.method === 'POST') {
                if (window.console) { console.log('POST Hit ' + request.url); }
                restService.add(request.body);
                const n = new HttpResponse({ status: 200 });
                return of(n);
            }
            if (request.method === 'GET') {
                if (window.console) { console.log('GET Hit ' + request.url); }
                if (restService.hasPartial(request.url)) {
                    const n = new HttpResponse({ status: 200, body: restService.getById(request.url) });
                    return of(n);
                } else {
                    const n = new HttpResponse({ status: 200, body: restService.result });
                    return of(n);
                }
            }
            if (request.method === 'PUT') {
                if (window.console) { console.log('UPDATE Hit ' + request.url); }
                restService.updateById(request.url, request.body);
                const n = new HttpResponse({ status: 200 });
                return of(n);
            }
            if (request.method === 'DELETE') {
                if (window.console) { console.log('DELETE Hit ' + request.url); }
                restService.deleteById(request.url);
                const n = new HttpResponse({ status: 200 });
                return of(n);
            }
        }
        if (window.console) { console.log('No hit for ' + request.url); }
        return next.handle(request);
    }
}