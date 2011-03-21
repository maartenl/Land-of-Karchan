<?php
// $Id: webform-confirmation.tpl.php,v 1.2 2010/02/11 22:05:27 quicksketch Exp $

/**
 * @file
 * Customize confirmation screen after successful submission.
 *
 * This file may be renamed "webform-confirmation-[nid].tpl.php" to target a
 * specific webform e-mail on your site. Or you can leave it
 * "webform-confirmation.tpl.php" to affect all webform confirmations on your
 * site.
 *
 * So now this file is specific to node 18, which is mudnewchar.
 *
 * Available variables:
 * - $node: The node object for this webform.
 * - $confirmation_message: The confirmation message input by the webform author.
 * - $sid: The unique submission ID of this submission.
 */
?>

<h1><?php print (trim($_SESSION["karchan_errormsg"]) == "" ? "Unknown error" : $_SESSION["karchan_errormsg"]) ?></h1>
<div class="webform-confirmation">
  <?php if ($confirmation_message): ?>
    <?php print $confirmation_message ?>
  <?php endif; ?>
    <?php
    if (substr($_SESSION["karchan_errormsg"],0,2) == 'Ok')
    {
      // head over to the inlogging.
      ?>
      <p>If everything is well, you should be directed to the game automatically.</p>
      <p>If this doesn't happen, click <a href="/node/24">here</a>.</p>
      <script type="text/javascript">
        window.location = "/node/24"
      </script>
      <?php
    } else
    if (trim($_SESSION["karchan_errormsg"]) == 'Player is already active.')
    {
       drupal_goto("/node/27");
    } else
    if (trim($_SESSION["karchan_errormsg"]) == 'Name contains illegal characters.')
    {
       drupal_goto("/node/28");
    } else
    if (trim($_SESSION["karchan_errormsg"]) == 'Password needs at least 5 characters.')
    {
       drupal_goto("/node/29");
    }
    ?>
</div>

<div class="links">
  <a href="<?php print url('node/'. $node->nid) ?>"><?php print t('Go back to the form') ?></a>
</div>
