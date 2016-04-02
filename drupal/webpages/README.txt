The great thing about Drupal is that webpages can be dynamically changed.

What is basically required for these pages is JQuery and Cookies.

However, this comes with it's own set of problems:
- the webpages are stored in a database, and cannot be easily changed
- the HTML of the webpages can be changed by Drupal or one of its editors
  without our consent, and it's hell to find out what we originally
  were typing. A good instance is the removal of onsubmit="function();return
  false;" by drupal.
- it bollocks up my indentation!
- it has no version control

So, in the interest of keeping a log *outside* of Drupal, here's the more
complex webpages that I feel require a little more.

+---------------------+---------------------------+--------------------------------------+
| Name of Play        | Filename                  | Drupal    | Liferay                  |
+---------------------+---------------------------+-----------+--------------------------+
| New Play            | play.html                 | /node/132 | /web/guest/play          |/karchangame/resources/game/[name]/play
                                                                                          /karchangame/resources/game/[name]/quit
|                     | charactersheet.html       | /node/135 |                          |
|                     | charactersheets.html      |
|                     | compose_mail.html         | /node/133 |                          |
|                     | edit_charactersheet.html  |
|                     | fortunes.html             |
|                     | karchan_buttons.html      |           | /web/guest/play          |
|                     | logon.html                | /node/131 | /weg/guest/logon         |/karchangame/resources/game/[name]/logon|
|                     | mail.html                 |
|                     | newchar.html              | /node/134 | /web/guest/new-character |
+---------------------+---------------------------+-----------+--------------------------+
|Guilds                                                                                  |
+---------------------+---------------------------+-----------+--------------------------+
|                     | guilds.html               | /node/10  |                 |
|                     | show_guild.html           | /node/136 |                 |
|                     | edit_guild.html           | /node/137 |                 |
|                     | create_guild.html         | /node/138 |                 |
+---------------------+---------------------------+-----------+--------------------------+
