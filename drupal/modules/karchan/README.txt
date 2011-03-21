Description
-----------
This module is specific for Land of Karchan use.

Requirements
------------
Drupal 7.x

Installation
------------
1. Copy the entire karchan directory the Drupal sites/all/modules directory.

2. Login as an administrator. Enable the module in the "Administer" -> "Site
   Building" -> "Modules"

3. (Optional) Edit the settings under "Administer" -> "Site configuration" ->
   "Karchan"

Upgrading from any previous version
-----------------------------------
1. Copy the entire karchan directory the Drupal modules directory.

2. Login as the FIRST user or change the $access_check in upgrade.php to FALSE

3. Run upgrade.php (at http://www.example.com/update.php)

$Id: README.txt,v 1.10 2010/02/10 02:30:53 quicksketch Exp $