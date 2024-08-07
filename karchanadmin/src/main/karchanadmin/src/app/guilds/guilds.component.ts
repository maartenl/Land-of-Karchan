import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import { AdminComponent } from '../admin/admin.component';
import { ToastService } from '../toast.service';
import { AdminRestService } from '../admin/admin-rest.service';
import { GuildsRestService } from '../guilds-rest.service';
import {Guild, Guildmember} from './guild.model';

@Component({
  selector: 'app-guilds',
  templateUrl: './guilds.component.html',
  styleUrls: ['./guilds.component.css']
})
export class GuildsComponent extends AdminComponent<Guild, string> implements OnInit {
  form: FormGroup;

  guildmembers: Guildmember[] = [] = new Array<Guildmember>(0);

  constructor(
    private guildsRestService: GuildsRestService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    const object = {
      name: null,
      title: null,
      guilddescription: null,
      guildurl: null,
      bossname: null,
      logonmessage: null,
      colour: null,
      imageurl: null,
      owner: null
    };
    this.form = this.formBuilder.group(object);
    this.makeItem();
    this.getItems();
  }

  ngOnInit(): void {
  }

  getItems() {
    this.guildsRestService.getAll()
      .subscribe({
        next: data => {
          this.items = data;
        }
      });
  }

  getRestService(): AdminRestService<Guild, string> {
    return this.guildsRestService;
  }

  setForm(item?: Guild) {
    const object = item === undefined ? {
      name: null,
      title: null,
      guilddescription: null,
      guildurl: null,
      bossname: null,
      logonmessage: null,
      colour: null,
      imageurl: null,
      owner: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  makeItem(): Guild {
    return new Guild();
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  getForm(): Guild {
    const formModel = this.form.value;

    const saveGuild: Guild = new Guild({
      name: formModel.name as string,
      title: formModel.title as string,
      guilddescription: formModel.guilddescription as string,
      guildurl: formModel.guildurl as string,
      bossname: formModel.bossname as string,
      colour: formModel.colour as string,
      logonmessage: formModel.logonmessage as string,
      imageurl: formModel.imageurl as string,
      creation: null,
      owner: formModel.owner as string
    });
    return saveGuild;
  }

  setItemById(id: string | undefined | null) {
    if (id === undefined || id === null) {
      return false;
    }
    this.guildsRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setGuild(data); }
      }
    });
    this.guildsRestService.getGuildmembers(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.guildmembers = data; }
      }
    });
    return false;
  }

  private setGuild(guild: Guild) {
    this.item = guild;
    this.form.reset({
      name: guild.name,
      title: guild.title,
      guilddescription: guild.guilddescription,
      guildurl: guild.guildurl,
      bossname: guild.bossname,
      logonmessage: guild.logonmessage,
      colour: guild.colour,
      imageurl: guild.imageurl,
      owner: guild.owner
    });
  }


}
