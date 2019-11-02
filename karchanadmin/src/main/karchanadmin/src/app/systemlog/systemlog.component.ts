import { Component, OnInit } from '@angular/core';

import { Systemlog } from './systemlog.model';
import { SystemlogService } from '../systemlog.service';

@Component({
  selector: 'app-systemlog',
  templateUrl: './systemlog.component.html',
  styleUrls: ['./systemlog.component.css']
})
export class SystemlogComponent implements OnInit {

  logs: Systemlog[];

  log: Systemlog;

  constructor(private systemlogService: SystemlogService) { }

  ngOnInit() {
    console.log(this.systemlogService);
    this.systemlogService.getLogs().subscribe(
      (result: Systemlog[]) => { // on success
        if (result !== undefined && result.length !== 0) {
          result.forEach((value) => {
            value.Timestamp = value.Timestamp.replace('[UTC]', '');
          });
          this.logs = result;
        }
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public setLog(log: Systemlog) {
    this.log = log;
  }

}
