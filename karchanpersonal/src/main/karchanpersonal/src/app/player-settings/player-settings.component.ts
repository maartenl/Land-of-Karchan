import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { Player } from './player.model';
import { Family } from './family.model';
import { PlayerService } from '../player.service';

@Component({
  selector: 'app-player-settings',
  templateUrl: './player-settings.component.html',
  styleUrls: ['./player-settings.component.css']
})
export class PlayerSettingsComponent implements OnInit {
  /**
   * the model
   */
  player: Player;

  playerForm: FormGroup;

  familyForm: FormGroup;

  constructor(private playerService: PlayerService,
              private formBuilder: FormBuilder) {
    this.player = new Player(); // dummy player
    this.createForms();
  }

  ngOnInit() {
    this.playerService.getPlayer()
      .subscribe(
        (result: any) => { // on success
          this.player = result;
          this.resetForm(result);
        },
        (err: any) => { // error
          // console.log("error", err);
        },
        () => { // on completion
        }
      );
  }

  createForms() {
    this.playerForm = this.formBuilder.group({
      title: '',
      homepageurl: '',
      imageurl: '',
      dateofbirth: '',
      cityofbirth: '',
      storyline: ''
    });
    this.familyForm = this.formBuilder.group({
      toname: '',
      description: ''
    });
  }

  resetForm(player: Player) {
    this.playerForm.reset({
      title: player.title,
      homepageurl: player.homepageurl,
      imageurl: player.imageurl,
      dateofbirth: player.dateofbirth,
      cityofbirth: player.cityofbirth,
      storyline: player.storyline
    });
  }

  setFamilyForm(family: Family) {
    this.familyForm.reset({
      toname: family.toname,
      description: family.description
    });
  }

  save() {
    const newPlayer = this.prepareSavePlayer();
    // TODO: add in the subscribe that the player is updated
    this.playerService.updatePlayer(newPlayer).subscribe();
  }

  prepareSavePlayer(): Player {
    const formModel = this.playerForm.value;

    // return new `Player` object containing a combination of original hero value(s)
    // and deep copies of changed form model values
    const savePlayer: Player = {
      name: this.player.name,
      title: formModel.title as string,
      sex: this.player.sex as string,
      description: this.player.description as string,
      guild: this.player.guild as string,
      homepageurl: formModel.homepageurl as string,
      imageurl: formModel.imageurl as string,
      cityofbirth: formModel.cityofbirth as string,
      dateofbirth: formModel.dateofbirth as string,
      storyline: formModel.storyline as string,
      familyvalues: this.player.familyvalues as Family[]
    };
    return savePlayer;
  }

  saveFamily() {
    const formModel = this.familyForm.value;
    const toname = formModel.toname as string;
    const description = formModel.description as string;
    let family: Family = this.getFamily(toname);
    if (family === null) {
      family = new Family();
      family.toname = toname;
      family.description = description;
      this.add(family);
      return;
    }
    family.toname = toname;
    family.description = description;
    this.update(family);
  }

  cancel() {
    this.resetForm(this.player);
  }

  deleteCharacter() {
    const game = this;
    if (confirm('Are you sure you want to delete this character? This cannot be undone.')) {
      // Delete it!
      this.playerService.deleteCharacter().subscribe(
        (result: any) => { // on success
          if (window.location !== window.parent.location) {
            window.parent.location.href = '/index.html?logout=true';
          }
          window.location.href = '/index.html?logout=true';
        }
      );
    }
  }

  private getFamily(toname: string): Family {
    const found: Family[] = this.player.familyvalues.filter((fam) => fam.toname === toname);
    if (found.length === 0) {
      return null;
    }
    return found[0];
  }

  delete(family: Family) {
    this.playerService.deleteFamily(family).subscribe(
      (result: any) => { // on success
        const index = this.player.familyvalues.indexOf(family, 0);
        if (index > -1) {
          this.player.familyvalues.splice(index, 1);
        }
      }
    );
  }

  add(family: Family) {
    this.playerService.updateFamily(family).subscribe(
      (result: any) => { // on success
        const length = this.player.familyvalues.push(family);
      }
    );
  }

  update(family: Family) {
    this.playerService.updateFamily(family).subscribe();
  }

  public getAllPossibleFamilyValues(): string[] {
    return Family.FAMILYVALUES;
  }

}
