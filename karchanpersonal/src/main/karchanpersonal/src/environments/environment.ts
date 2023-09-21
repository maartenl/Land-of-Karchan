// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  website: 'http://localhost:8080',
  PRIVATE_URL: '/karchangame/resources/private/[player]',
  CHARACTERSHEET_URL: '/karchangame/resources/private/[player]/charactersheet',
  MAIL_URL: '/karchangame/resources/private/[player]/mail',
  SENTMAIL_URL: '/karchangame/resources/private/[player]/sentmail',
  HASNEWMAIL_URL: '/karchangame/resources/private/[player]/newmail',
  FAMILY_URL: '/karchangame/resources/private/[player]/charactersheet/familyvalues/',
  GUILD_URL: '/karchangame/resources/private/[player]/guild',
  GUILDHOPEFULS_URL: '/karchangame/resources/private/[player]/guild/hopefuls',
  GUILDRANKS_URL: '/karchangame/resources/private/[player]/guild/ranks',
  GUILDMEMBERS_URL: '/karchangame/resources/private/[player]/guild/members',
  WIKIPAGES_URL: '/karchangame/resources/wikipages',
  WIKIPAGES_PREVIEW_URL: '/wiki/preview',
  PICTURES_URL: '/karchangame/resources/private/[player]/pictures',
  GAME_URL: '/karchangame/resources/game/[player]/',
  WHO_URL: '/karchangame/resources/public/who'
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
