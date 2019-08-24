// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  website: 'http://localhost:8080',
  PRIVATE_URL: '/karchangame/resources/private/[player]',
  CHARACTERSHEET_URL: 'assets/charactersheet.json',
  MAIL_URL: 'assets/mail.json',
  HASNEWMAIL_URL: 'assets/hasnewmail.json',
  FAMILY_URL: '/karchangame/resources/private/Karn/charactersheet/familyvalues/',
  GUILD_URL: '/assets/guild.json',
  GUILDHOPEFULS_URL: '/assets/guildhopefuls.json',
  GUILDRANKS_URL: '/assets//guildranks.json',
  GUILDMEMBERS_URL: '/assets/guildmembers.json',
  WIKIPAGES_URL: '/assets/wikipages',
  WIKIPAGES_PREVIEW_URL: '/assets/wikipages/preview.html',
  PICTURES_URL: '/assets/pictures.json',
  GAME_URL: '/karchangame/resources/game/[player]/'
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
