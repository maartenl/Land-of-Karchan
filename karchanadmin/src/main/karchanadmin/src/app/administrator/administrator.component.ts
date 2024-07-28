import {Component, OnInit} from '@angular/core';

import {AdministratorsRestService} from '../administrators-rest.service';
import {Administrator} from './administrator.model';
import {ToastService} from '../toast.service';

@Component({
  selector: 'app-administrator',
  templateUrl: './administrator.component.html',
  styleUrls: ['./administrator.component.css']
})
export class AdministratorComponent implements OnInit {

  administrator: Administrator = new Administrator();

  constructor(private administratorsRestService: AdministratorsRestService,
              private toastService: ToastService) {
    this.administratorsRestService.get().subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.setAdministrator(data);
        }
        console.log(data);
      }
    });
  }

  ngOnInit(): void {
  }

  setAdministrator(admin: Administrator) {
    this.administrator = admin;
  }

}
