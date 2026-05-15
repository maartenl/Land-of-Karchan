import {Component, inject, OnInit, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';

import {Player} from './player.model';
import {Family} from './family.model';
import {PlayerService} from '../player.service';
import {ToastService} from '../toast.service';
import {ChatlogService} from '../chatlog.service';
import {Logger, LogLevel} from "../consolelog.service";
import {ThemeService} from "../theme.service";

export interface PlayerSettingsData {
  title: string
  familyname: string
  homepageurl: string
  imageurl: string
  dateofbirth: string
  cityofbirth: string
  storyline: string
  websockets: boolean
  debuglog: LogLevel
}

export interface FamilyData {
  toname: string
  description: string
}

export interface PasswordData {
  oldpassword: string
  password: string
  password2: string
}

@Component({
  selector: 'app-player-settings',
  imports: [FormField],
  templateUrl: './player-settings.html',
  styleUrl: './player-settings.css',
})
export class PlayerSettings implements OnInit {

  private playerService = inject(PlayerService);
  private chatlogService = inject(ChatlogService);
  private toastService = inject(ToastService);
  private themeService = inject(ThemeService);

  /**
   * the model
   */
  player = signal(new Player());

  playerModel = signal<PlayerSettingsData>({
    title: '',
    familyname: '',
    homepageurl: '',
    imageurl: '',
    dateofbirth: '',
    cityofbirth: '',
    storyline: '',
    websockets: true,
    debuglog: Logger.getLogLevel() as LogLevel,
  })

  playerForm = form(this.playerModel);

  familyModel = signal<FamilyData>({
    toname: '',
    description: ''
  })

  familyForm = form(this.familyModel)

  passwordModel = signal<PasswordData>({
    oldpassword: '',
    password: '',
    password2: '',
  })

  resetpasswordForm = form(this.passwordModel)

  ngOnInit() {
    this.playerService.getPlayer()
      .subscribe({
          next: (result: any) => { // on success
            this.player.set(result as Player);
            this.resetForm(result);
          }
        }
      );
  }

  resetForm(player: Player) {
    this.playerModel.set({
      title: player.title || '',
      familyname: player.familyname || '',
      homepageurl: player.homepageurl || '',
      imageurl: player.imageurl || '',
      dateofbirth: player.dateofbirth || '',
      cityofbirth: player.cityofbirth || '',
      storyline: player.storyline || '',
      websockets: player.websockets || true,
      debuglog: Logger.getLogLevel() as LogLevel,
    });
  }

  setFamilyForm(family: Family) {
    this.familyModel.set({
      toname: family.toname,
      description: family.description
    });
  }

  onSubmitPlayer(event: Event) {
    event.preventDefault();
    const newPlayer = this.prepareSavePlayer();
    if (newPlayer.websockets) {
      this.chatlogService.enableWebsockets();
    } else {
      this.chatlogService.disableWebsockets();
    }
    this.playerService.updatePlayer(newPlayer).subscribe({
        next: (result: any) => { // on success
          this.toastService.showMessage("Settings successfully updated.", "Updated...");
        }
      }
    );
  }

  darkmode() {
    this.themeService.toggleTheme();
  }

  private prepareSavePlayer(): Player {
    const formModel = this.playerModel();

    const logLevel: LogLevel = formModel.debuglog as LogLevel;
    Logger.setLogLevel(logLevel);
    // return new `Player` object containing a combination of original hero value(s)
    // and deep copies of changed form model values
    const savePlayer: Player = {
      name: this.player().name,
      title: formModel.title as string,
      familyname: formModel.familyname as string,
      sex: this.player().sex as string,
      description: this.player().description as string,
      guild: this.player().guild as string,
      homepageurl: formModel.homepageurl as string,
      imageurl: formModel.imageurl as string,
      cityofbirth: formModel.cityofbirth as string,
      dateofbirth: formModel.dateofbirth as string,
      storyline: formModel.storyline as string,
      familyvalues: this.player().familyvalues as Family[],
      websockets: formModel.websockets as boolean,
    };
    return savePlayer;
  }

  onSubmitFamily(event: Event) {
    event.preventDefault();
    const formModel = this.familyModel();
    const toname = formModel.toname as string;
    const description = formModel.description as string;
    let family: Family | null = this.getFamily(toname);
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

  onSubmitResetPassword(event: Event) {
    event.preventDefault();
    const formModel = this.passwordModel();
    const oldpassword = formModel.oldpassword as string;
    const password = formModel.password as string;
    const password2 = formModel.password2 as string;
    this.playerService.resetPassword(this.player().name, oldpassword, password, password2).subscribe((result: any) => {
      this.toastService.showMessage("Password reset.", "Reset...");
    })
    ;
    this.passwordModel.set({
      oldpassword: '',
      password: '',
      password2: '',
    })
  }

  cancel() {
    this.resetForm(this.player());
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

  private getFamily(toname: string):
    Family | null {
    const found: Family[] = this.player().familyvalues.filter((fam) => fam.toname === toname);
    if (found.length === 0) {
      return null;
    }
    return found[0];
  }

  delete(family: Family) {
    this.playerService.deleteFamily(family).subscribe(
      (result: any) => { // on success
        this.player.update(player => {
            const index = player.familyvalues.indexOf(family, 0);
            if (index > -1) {
              player.familyvalues.splice(index, 1);
            }
            return player;
          }
        )
        this.toastService.showMessage("Family entry successfully deleted.", "Deleted...");
      }
    );
  }

  add(family: Family) {
    this.playerService.updateFamily(family).subscribe(
      (result: any) => { // on success
        this.player.update(player => {
          player.familyvalues.push(family);
          return player;
        });
        this.toastService.showMessage("Family entry successfully added.", "Added...");
      }
    );
  }

  update(family: Family) {
    this.playerService.updateFamily(family).subscribe((result: any) => {
        this.toastService.showMessage("Family entry successfully updated.", "Updated...");
      }
    );
  }

  public getAllPossibleFamilyValues(): string[] {
    return Family.FAMILYVALUES;
  }

  getDarkmodeLabel(): string {
    if (this.themeService.isDarkThemeActive()) {
      return "Turn darkmode off";
    } else {
      return "Turn darkmode on";
    }
  }

}
