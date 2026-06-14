import {Component, inject, OnInit, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {AdminComponent} from '../admin/admin.component';
import {Guild, Guildmember} from './guild.model';
import {ColDef, SelectionChangedEvent} from "ag-grid-community";
import {ToastService} from "../toast.service";
import {GuildsRestService} from '../guilds-rest.service';
import {Logger} from '../consolelog.service';
import {LinkRenderer} from '../link-renderer/link-renderer';
import {AgGridAngular} from 'ag-grid-angular';
import {rowSelection} from '../aggrid.utils';

export interface GuildData {
  name: string;
  title: string;
  guilddescription: string;
  guildurl: string;
  bossname: string;
  logonmessage: string;
  colour: string;
  imageurl: string;
  owner: string;
}

@Component({
  selector: 'app-guilds',
  imports: [FormField,
    AgGridAngular
  ],
  templateUrl: './guilds.html',
  styleUrl: './guilds.css',
})
export class Guilds extends AdminComponent<Guild, string> implements OnInit {

  override restService = inject(GuildsRestService);
  override toastService = inject(ToastService);

  guildmembers = signal<Guildmember[]>([]);

  override colDefs: ColDef[] = [
    {field: "name", filter: true, width: 150},
    {field: "title", filter: true, width: 150},
    {field: "bossname", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/characters"}},
    {field: "owner", filter: true, width: 100},
    {field: "creation"},
  ];

  guildmembersColDef: ColDef[] = [
    {
      field: "name", filter: true, width: 150, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/characters"}
    },
    {field: "guildrank", filter: true, width: 150},
  ];

  guildModel = signal<GuildData>({
    name: "",
    title: "",
    guilddescription: "",
    guildurl: "",
    bossname: "",
    logonmessage: "",
    colour: "",
    imageurl: "",
    owner: ""
  });

  form = form(this.guildModel);

  ngOnInit(): void {
  }

  override setForm(): void {
    const guild = this.item();
    Logger.logEntering("setForm");
    this.guildModel.set({
      name: guild.name ?? "",
      title: guild.title ?? "",
      guilddescription: guild.guilddescription ?? "",
      guildurl: guild.guildurl ?? "",
      bossname: guild.bossname ?? "",
      logonmessage: guild.logonmessage ?? "",
      colour: guild.colour ?? "",
      imageurl: guild.imageurl ?? "",
      owner: guild.owner ?? ""
    });
  }

  override makeItem(): Guild {
    return new Guild();
  }

  override onSelectionChanged = (event: SelectionChangedEvent) => {
    this.setItemById(event.api.getSelectedRows()[0].name);
  };

  override getForm(): Guild {
    const guild = this.item();
    const formModel = this.guildModel();
    guild.name = formModel.name;
    guild.title = formModel.title;
    guild.guilddescription = formModel.guilddescription == "" ? null : formModel.guilddescription;
    guild.guildurl = formModel.guildurl == "" ? null : formModel.guildurl;
    guild.bossname = formModel.bossname == "" ? null : formModel.bossname;
    guild.colour = formModel.colour == "" ? null : formModel.colour;
    guild.logonmessage = formModel.logonmessage == "" ? null : formModel.logonmessage;
    guild.imageurl = formModel.imageurl == "" ? null : formModel.imageurl;
    guild.owner = formModel.owner == "" ? null : formModel.owner;
    return guild;
  }

  override setItemById(id: string | null | undefined): boolean {
    if (id === undefined || id === null) {
      return false;
    }
    this.restService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.item.set(data);
          this.setForm();
        }
      }
    });
    this.restService.getGuildmembers(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.guildmembers.set(data);
        }
      }
    });
    return false;
  }

  protected readonly rowSelection = rowSelection;
}
