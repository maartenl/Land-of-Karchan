The great thing about Drupal is that webpages can be dynamically changed.

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

+---------------------+-----------------------+----------------------------+
| Name of Play        | Filename              | Node in drupal             |
+---------------------+-----------------------+----------------------------+
| New Play            | play.html             | /node/132                  |
|                     | charactersheet.html   | /node/135                  |
|                     | charactersheets.html  |
|                     | compose_mail.html     | /node/133                  |
|                     | edit_charactersheet.html
|                     | fortunes.html
|                     | karchan_buttons.html
|                     | logon.html            | /node/131                  |
|                     | mail.html
|                     | newchar.html          | /node/134                  |
+---------------------+-----------------------+----------------------------+
|Guilds                                                                    |
+---------------------+-----------------------+----------------------------+
|                     | guilds.html           | /node/10                   |
|                     | show_guild.html       | /node/136                  |
|                     | edit_guild.html       | /node/137                  |
+---------------------+-----------------------+----------------------------+
