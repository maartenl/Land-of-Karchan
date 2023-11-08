import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {Systemlog} from './systemlog.model';
import {SystemlogService} from '../systemlog.service';
import {ToastService} from '../toast.service';
import {Logger} from "../consolelog.service";

@Component({
  selector: 'app-systemlog',
  templateUrl: './systemlog.component.html',
  styleUrls: ['./systemlog.component.css']
})
export class SystemlogComponent implements OnInit {

  logs: Systemlog[] = new Array<Systemlog>(0);

  log: Systemlog | null = null;

  form: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private systemlogService: SystemlogService,
    private toastService: ToastService) {
    const object = {
      nameSearch: null,
      fromSearch: null,
      toSearch: null,
    };
    this.form = this.formBuilder.group(object);
  }

  ngOnInit() {
    console.log(this.systemlogService);
  }

  public setLog(log: Systemlog) {
    this.log = log;
  }

  public search() {
    const formModel = this.form.value;
    var name = formModel.nameSearch;
    var fromdate = formModel.fromSearch;
    var todate = formModel.toSearch;
    Logger.log('search ' + name + ' ' + fromdate + ' ' + todate);

    this.systemlogService.getLogs(name, fromdate, todate)
      .subscribe({
          next: (result: Systemlog[]) => {
            if (result !== undefined) {
              this.logs = result;
            }
            Logger.log('search done');
            this.toastService.show('Logging messages successfully retrieved.', {
              delay: 3000,
              autohide: true,
              headertext: 'Logging...'
            });
          },
          error: (err: any) => {
            Logger.logError('error in getLogs', err);
          },
          complete: () => { // on completion
          }
        }
      );
  }

}
