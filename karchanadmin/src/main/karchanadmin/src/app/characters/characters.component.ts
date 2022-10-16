import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {MudCharacter} from './character.model';
import {FormBuilder, FormGroup} from '@angular/forms';

import {AdminComponent} from '../admin/admin.component';
import {ToastService} from '../toast.service';
import {AdminRestService} from '../admin/admin-rest.service';
import {Attribute} from '../attribute.model';
import {CharactersRestService} from '../characters-rest.service';
import {AttributesRestService} from '../attributes-rest.service';

@Component({
  selector: 'app-characters',
  templateUrl: './characters.component.html',
  styleUrls: ['./characters.component.css']
})
export class CharactersComponent extends AdminComponent<MudCharacter, string> implements OnInit {
  form: FormGroup;

  attributeForm: FormGroup;

  attributes: Array<Attribute> = [];

  attribute: Attribute | null = null;

  SearchTerms = class {
    owner: string | null = null;
    ip: string | null = null;
    name: string | null = null;
    room: string | null = null;
  };

  searchTerms = new this.SearchTerms();

  constructor(
    private charactersRestService: CharactersRestService,
    private attributesRestService: AttributesRestService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    const object = {
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
      birth: null,
      lastlogin: null,
      copper: null,
      notes: null,
      address: null,
      realname: null,
      email: null,
      newpassword: null,
      owner: null
    };
    this.form = this.formBuilder.group(object);
    const attribute = {
      id: null,
      name: null,
      value: null,
      valueType: null
    }
    this.attributeForm = this.formBuilder.group(attribute);
    this.makeItem();
    this.getItems();
  }

  updateOwnerSearch(value: string) {
    this.searchTerms.owner = value.trim() === '' ? null : value;
    this.getItems();
  }

  updateNameSearch(value: string) {
    this.searchTerms.name = value.trim() === '' ? null : value;
    this.getItems();
  }

  updateIpSearch(value: string) {
    this.searchTerms.ip = value.trim() === '' ? null : value;
    this.getItems();
  }

  updateRoomSearch(value: string) {
    this.searchTerms.room = value.trim() === '' ? null : value;
    this.getItems();
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
    const name: string | null = this.route.snapshot.paramMap.get('name');
    if (name === undefined || name === null) {
      return;
    }
    this.setItemById(name);
  }

  getItems() {
    this.charactersRestService.getAll().subscribe({
      next: (data: MudCharacter[]) => {
        const ownerFilter = (character: MudCharacter) => this.searchTerms.owner === undefined ||
          this.searchTerms.owner === null ||
          this.searchTerms.owner === character.owner;
        const nameFilter = (character: MudCharacter) => this.searchTerms.name === undefined ||
          this.searchTerms.name === null ||
          (character.name !== null && character.name.includes(this.searchTerms.name));
        const ipFilter = (character: MudCharacter) => this.searchTerms.ip === undefined ||
          this.searchTerms.ip === null ||
          (character.address !== null && character.address.includes(this.searchTerms.ip));
        const roomFilter = (character: MudCharacter) => this.searchTerms.room === undefined ||
          this.searchTerms.room === null ||
          (character.room !== null && character.room === new Number(this.searchTerms.room));
        this.items = data.filter(ownerFilter).filter(nameFilter).filter(ipFilter).filter(roomFilter);
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
      birth: null,
      lastlogin: null,
      copper: null,
      notes: null,
      address: null,
      realname: null,
      email: null,
      newpassword: null,
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
      god: formModel.god as string,
      room: formModel.room as number,
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
      newpassword: formModel.newpassword as string,
      owner: formModel.owner as string
    });
    return saveCharacter;
  }

  setItemById(name: string | undefined | null) {
    if (name === undefined || name === null) {
      return false;
    }
    this.charactersRestService.get(name).subscribe({
      next: (data: MudCharacter) => {
        if (data !== undefined) {
          this.setCharacter(data);
        }
      }
    });
    return false;
  }

  setAttribute(attribute: Attribute) {
    this.attribute = attribute;
    this.setAttributeForm(attribute);
    return false;
  }

  deleteAttribute(attribute: Attribute) {
    if (window.console) {
      console.log("deleteAttribute " + attribute);
    }
    this.attributesRestService.delete(attribute).subscribe(
      (result: any) => { // on success
        const index = this.attributes.indexOf(attribute, 0);
        if (index > -1) {
          this.attributes.splice(index, 1);
        }
        this.toastService.show('Attribute successfully deleted.', {
          delay: 3000,
          autohide: true,
          headertext: 'Deleted...'
        });
      }
    );
    return false;
  }

  updateAttribute() {
    const attribute = this.getAttributeForm();
    if (window.console) {
      console.log("updateAttribute " + attribute);
    }
    if (attribute === null || attribute === undefined) {
      return false;
    }
    this.attributesRestService.update(attribute).subscribe(
      (result: any) => { // on success
        this.attributes = this.attributes.map(u => u.id !== attribute.id ? u : attribute);
        this.toastService.show('Attribute successfully updated.', {
          delay: 3000,
          autohide: true,
          headertext: 'Updated...'
        });
      }
    );
    return false;
  }

  createAttribute() {
    const attribute = this.getAttributeForm();
    if (window.console) {
      console.log("createAttribute " + attribute);
    }
    if (attribute === null || attribute === undefined) {
      return false;
    }
    attribute.id = null;
    this.attributesRestService.update(attribute).subscribe(
      (result: any) => { // on success
        this.attributes.push(attribute);
        this.toastService.show('Attribute successfully created.', {
          delay: 3000,
          autohide: true,
          headertext: 'Created...'
        });
      }
    );
    return false;
  }

  cancelAttribute() {
    this.setAttributeForm();
    return false;
  }

  setAttributeForm(item?: Attribute) {
    const object = item === undefined ? {
      id: null,
      name: null,
      value: null,
      valueType: null,
    } : item;
    if (this.attributeForm === undefined) {
      this.attributeForm = this.formBuilder.group(object);
    } else {
      this.attributeForm.reset(object);
    }
  }

  getAttributeForm(): Attribute | null {
    const formModel = this.attributeForm.value;

    if (this.item === null || this.item === undefined) {
      return null;
    }

    const saveAttribute: Attribute = new Attribute({
      id: formModel.id as number,
      name: formModel.name as string,
      value: formModel.value as string,
      valueType: formModel.valueType as string,
      objecttype: 'PERSON',
      objectid: this.item.name as string,
    });
    return saveAttribute;
  }

  private setCharacter(character: MudCharacter) {
    this.item = character;
    this.attributesRestService.getFromCharacter(character).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.attributes = data;
        }
      }
    })
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
      birth: character.birth,
      lastlogin: character.lastlogin,
      copper: character.copper,
      notes: character.notes,
      address: character.address,
      realname: character.realname,
      email: character.email,
      newpassword: null,
      owner: character.owner
    });
  }

}
