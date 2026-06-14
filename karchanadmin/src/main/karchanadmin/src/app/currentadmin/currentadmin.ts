import {Component, inject, OnInit, signal} from '@angular/core';
import {Administrator} from './administrator.model';
import {AdministratorsRestService} from '../administrators-rest.service';
import {Logger} from '../consolelog.service';

@Component({
  selector: 'app-currentadmin',
  imports: [],
  templateUrl: './currentadmin.html',
  styleUrl: './currentadmin.css',
})
export class Currentadmin implements OnInit {
  administratorsRestService = inject(AdministratorsRestService);

  administrator= signal(new Administrator());

  ngOnInit(): void {
    this.administratorsRestService.get().subscribe({
      next: (data: Administrator) => {
        if (data !== undefined) {
          this.administrator.set(data);
        }
        Logger.logObject(data);
      }
    });
  }

}
