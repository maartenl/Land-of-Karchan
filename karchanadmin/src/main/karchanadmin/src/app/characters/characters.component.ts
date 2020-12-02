import { Component, OnInit } from '@angular/core';
import { MudCharacter } from './character.model';
import { FormGroup, FormBuilder } from '@angular/forms';

import { AdminComponent } from '../admin/admin.component';
import { ToastService } from '../toast.service';
import { AdminRestService } from '../admin/admin-rest.service';
import { CharactersRestService } from '../characters-rest.service';

@Component({
  selector: 'app-characters',
  templateUrl: './characters.component.html',
  styleUrls: ['./characters.component.css']
})
export class CharactersComponent extends AdminComponent<MudCharacter, string> implements OnInit {
  form: FormGroup;

  SearchTerms = class {
    owner: string;
    ip: string;
    room: string;
  };

  searchTerms = new this.SearchTerms();

  constructor(
    private charactersRestService: CharactersRestService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    this.setForm();
    this.makeItem();
    this.getItems();
  }

  updateOwnerSearch(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.owner = value;
    this.getItems();
  }

  updateIpSearch(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.ip = value;
    this.getItems();
  }

  updateRoomSearch(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.room = value;
    this.getItems();
  }

  ngOnInit() {
  }

  getItems() {
    this.charactersRestService.getAll().subscribe({
    next: data => {
      const ownerFilter = character => this.searchTerms.owner === undefined ||
        this.searchTerms.owner === null ||
        this.searchTerms.owner === character.owner;
      const ipFilter = character => this.searchTerms.ip === undefined ||
        this.searchTerms.ip === null ||
        (character.address !== null && character.address.includes(this.searchTerms.ip));
      const roomFilter = character => this.searchTerms.room === undefined ||
        this.searchTerms.room === null ||
        character.room == this.searchTerms.room;
      this.items = data.filter(ownerFilter).filter(ipFilter).filter(roomFilter);
    }
  });
}

  getRestService(): AdminRestService<MudCharacter, string> {
    return this.charactersRestService;
  }

  setForm(item?: MudCharacter) {
    const object = item === undefined ? {
      name: null,
      image: null,
      title: null,
      room: null,
      god: null,
      race: null,
      sex: null,
      age: null,
      height: null,
      width: null,
      complexion: null,
      eyes: null,
      face: null,
      hair: null,
      beard: null,
      arm: null,
      leg: null,
      copper: null,
      notes: null,
      address: null,
      realname: null,
      email: null,
      owner: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  makeItem(): MudCharacter {
    return new MudCharacter();
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  getForm(): MudCharacter {
    const formModel = this.form.value;

    const saveCharacter: MudCharacter = new MudCharacter({
      name: formModel.name as string,
      image: formModel.image as string,
      title: formModel.title as string,
      god: formModel.title as string,
      room: formModel.title as number,
      race: formModel.race as string,
      sex: formModel.sex as string,
      age: formModel.age as string,
      height: formModel.height as string,
      width: formModel.width as string,
      complexion: formModel.complexion as string,
      eyes: formModel.eyes as string,
      face: formModel.face as string,
      hair: formModel.hair as string,
      beard: formModel.beard as string,
      arm: formModel.arm as string,
      leg: formModel.leg as string,
      copper: formModel.copper as number,
      notes: formModel.notes as string,
      address: formModel.address as string,
      realname: formModel.realname as string,
      email: formModel.email as string,
      owner: formModel.owner as string
    });
    return saveCharacter;
  }

  setItemById(id: string) {
    this.charactersRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setCharacter(data); }
      }
    });
    return false;
  }


  private setCharacter(character: MudCharacter) {
    this.item = character;
    this.form.reset({
      name: character.name,
      image: character.image,
      title: character.title,
      room: character.room,
      god: character.god,
      race: character.race,
      sex: character.sex,
      age: character.age,
      height: character.height,
      width: character.width,
      complexion: character.complexion,
      eyes: character.eyes,
      face: character.face,
      hair: character.hair,
      beard: character.beard,
      arm: character.arm,
      leg: character.leg,
      copper: character.copper,
      notes: character.notes,
      address: character.address,
      realname: character.realname,
      email: character.email,
      owner: character.owner
    });
  }

}
