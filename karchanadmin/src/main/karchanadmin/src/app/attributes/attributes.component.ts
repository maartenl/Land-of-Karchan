import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

import {ToastService} from '../toast.service';
import {Attribute} from '../attribute.model';
import {AttributesRestService} from '../attributes-rest.service';

@Component({
  selector: 'app-attributes',
  templateUrl: './attributes.component.html',
  styleUrls: ['./attributes.component.css']
})
export class AttributesComponent implements OnInit {

  attributeForm: FormGroup;

  attributes: Array<Attribute> = [];

  attribute: Attribute | null = null;

  SearchTerms = class {
    name: string | null = null;
  };

  searchTerms = new this.SearchTerms();

  constructor(
    private attributesRestService: AttributesRestService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    const attribute = {
      id: null,
      name: null,
      value: null,
      valueType: null
    }
    this.attributeForm = this.formBuilder.group(attribute);
    this.getItems();
  }

  updateNameSearch(value: string) {
    this.searchTerms.name = value.trim() === '' ? null : value;
    this.getItems();
  }

  ngOnInit(): void {
    if (window.console) {
      console.log('ngOnInit');
    }
    const name: string | null = this.route.snapshot.paramMap.get('name');
    if (name === undefined || name === null) {
      return;
    }
    this.searchTerms.name = name;
    this.getItems();
  }

  getItems() {
    if (this.searchTerms.name === null || this.searchTerms.name === undefined) {
      return;
    }
    this.attributesRestService.getAll(this.searchTerms.name).subscribe({
      next: (data: Attribute[]) => {
        this.attributes = data;
      }
    });
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
      objecttype: null,
      objectid: null,
    } : item;
    if (this.attributeForm === undefined) {
      this.attributeForm = this.formBuilder.group(object);
    } else {
      this.attributeForm.reset(object);
    }
  }

  getAttributeForm(): Attribute | null {
    const formModel = this.attributeForm.value;

    if (this.attribute === null || this.attribute === undefined) {
      return null;
    }

    const saveAttribute: Attribute = new Attribute({
      id: formModel.id as number,
      name: formModel.name as string,
      value: formModel.value as string,
      valueType: formModel.valueType as string,
      objecttype: this.attribute.objecttype as string,
      objectid: this.attribute.objectid as string,
    });
    return saveAttribute;
  }
}
