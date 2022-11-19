import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {Systemlog} from './systemlog.model';
import {SystemlogService} from '../systemlog.service';
import {ToastService} from '../toast.service';

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
      dateSearch: null,
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
    if (window.console) {
      console.log("search");
    }
    const formModel = this.form.value;
    var name = formModel.nameSearch;
    console.log(name);
    var creationdate = formModel.dateSearch;
    console.log(creationdate);

    this.systemlogService.getLogs(name, creationdate).subscribe(
      (result: Systemlog[]) => { // on success
        if (result !== undefined && result.length !== 0) {
          this.logs = result;
        }
        if (window.console) {
          console.log("search done");
        }
        this.toastService.show('Logging messages successfully retrieved.', {
          delay: 3000,
          autohide: true,
          headertext: 'Logging...'
        });
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

}
