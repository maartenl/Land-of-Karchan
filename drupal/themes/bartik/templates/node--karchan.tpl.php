<?php
// $Id: node.tpl.php,v 1.2 2010/12/01 00:18:15 webchick Exp $

/**
 * @file
 * Bartik's theme implementation to display a node.
 *
 * Available variables:
 * - $title: the (sanitized) title of the node.
 * - $content: An array of node items. Use render($content) to print them all,
 *   or print a subset such as render($content['field_example']). Use
 *   hide($content['field_example']) to temporarily suppress the printing of a
 *   given element.
 * - $user_picture: The node author's picture from user-picture.tpl.php.
 * - $date: Formatted creation date. Preprocess functions can reformat it by
 *   calling format_date() with the desired parameters on the $created variable.
 * - $name: Themed username of node author output from theme_username().
 * - $node_url: Direct url of the current node.
 * - $display_submitted: Whether submission information should be displayed.
 * - $submitted: Submission information created from $name and $date during
 *   template_preprocess_node().
 * - $classes: String of classes that can be used to style contextually through
 *   CSS. It can be manipulated through the variable $classes_array from
 *   preprocess functions. The default values can be one or more of the
 *   following:
 *   - node: The current template type, i.e., "theming hook".
 *   - node-[type]: The current node type. For example, if the node is a
 *     "Blog entry" it would result in "node-blog". Note that the machine
 *     name will often be in a short form of the human readable label.
 *   - node-teaser: Nodes in teaser form.
 *   - node-preview: Nodes in preview mode.
 *   The following are controlled through the node publishing options.
 *   - node-promoted: Nodes promoted to the front page.
 *   - node-sticky: Nodes ordered above other non-sticky nodes in teaser
 *     listings.
 *   - node-unpublished: Unpublished nodes visible only to administrators.
 * - $title_prefix (array): An array containing additional output populated by
 *   modules, intended to be displayed in front of the main title tag that
 *   appears in the template.
 * - $title_suffix (array): An array containing additional output populated by
 *   modules, intended to be displayed after the main title tag that appears in
 *   the template.
 *
 * Other variables:
 * - $node: Full node object. Contains data that may not be safe.
 * - $type: Node type, i.e. story, page, blog, etc.
 * - $comment_count: Number of comments attached to the node.
 * - $uid: User ID of the node author.
 * - $created: Time the node was published formatted in Unix timestamp.
 * - $classes_array: Array of html class attribute values. It is flattened
 *   into a string within the variable $classes.
 * - $zebra: Outputs either "even" or "odd". Useful for zebra striping in
 *   teaser listings.
 * - $id: Position of the node. Increments each time it's output.
 *
 * Node status variables:
 * - $view_mode: View mode, e.g. 'full', 'teaser'...
 * - $teaser: Flag for the teaser state (shortcut for $view_mode == 'teaser').
 * - $page: Flag for the full page state.
 * - $promote: Flag for front page promotion state.
 * - $sticky: Flags for sticky post setting.
 * - $status: Flag for published status.
 * - $comment: State of comment settings for the node.
 * - $readmore: Flags true if the teaser content of the node cannot hold the
 *   main body content.
 * - $is_front: Flags true when presented in the front page.
 * - $logged_in: Flags true when the current user is a logged-in member.
 * - $is_admin: Flags true when the current user is an administrator.
 *
 * Field variables: for each field instance attached to the node a corresponding
 * variable is defined, e.g. $node->body becomes $body. When needing to access
 * a field's raw values, developers/themers are strongly encouraged to use these
 * variables. Otherwise they will have to explicitly specify the desired field
 * language, e.g. $node->body['en'], thus overriding any language negotiation
 * rule that was previously applied.
 *
 * @see template_preprocess()
 * @see template_preprocess_node()
 * @see template_process()
 */
?>
<div id="node-<?php print $node->nid; ?>" class="<?php print $classes; ?> clearfix"<?php print $attributes; ?>>

  <?php print render($title_prefix); ?>
  <?php if (!$page): ?>
    <h2<?php print $title_attributes; ?>>
      <a href="<?php print $node_url; ?>"><?php print $title; ?></a>
    </h2>
  <?php endif; ?>
  <?php print render($title_suffix); ?>

  <?php if ($display_submitted): ?>
    <div class="meta submitted">
      <?php print $user_picture; ?>
      <?php print $submitted; ?>
    </div>
  <?php endif; ?>

  <div class="content clearfix"<?php print $content_attributes; ?>>
    <?php
      // We hide the comments and links now so that we can render them later.
      hide($content['comments']);
      hide($content['links']);
      print render($content);
    ?>
  </div>

  <?php
    // Remove the "Add new comment" link on the teaser page or if the comment
    // form is being displayed on the same page.
    if ($teaser || !empty($content['comments']['comment_form'])) {
      unset($content['links']['comment']['#links']['comment-add']);
    }
    // Only display the wrapper div if there are links.
    $links = render($content['links']);
    if ($links):
  ?>
    <div class="link-wrapper">
      <?php print $links; ?>
    </div>
  <?php endif; ?>
  <?php
if (!isset($_COOKIE["karchanpassword"]))
{
    drupal_goto("/node/30");
}
if (!isset($_COOKIE["karchanname"]))
{
    drupal_goto("/node/30");
}
if (!isset($_SESSION["frames"]))
{
    drupal_goto("/node/30");
}
    $frames = $_SESSION["frames"];
    $password = $_COOKIE["karchanpassword"];
    $name = $_COOKIE["karchanname"];
    $command = "l";
if (isset($_GET["command"]))
{
    $command = $_GET["command"];
}
if (isset($_POST["command"]))
{
    $command = $_POST["command"];
}
$bigtalk = false;
$bigtalk = (isset($_POST["bigtalk"])) || (isset($_GET["bigtalk"]));

if (substr($command, 0, 3) == "<p>") 
{
  $command = substr($command, 3);
}
if (substr($command, -4) == "</p>")
{
  $command = substr($command, 0, -4);
}


// Hack prevention.
//	$headers = apache_request_headers();
//	header()
//	setcookie();

//	foreach ($headers as $header => $value) 
//	{
//		echo "$header: $value <br />\n";
//	}
	//$fp = fsockopen ($server_host, $server_port, $errno, $errstr, 30);
	$fp = fsockopen ("localhost", 3340, $errno, $errstr, 30);
	if (!$fp) 
	{
	        $_SESSION["karchan_errormsg"] = "Could not open socket.";
        	ob_end_flush();
	        return;
        }
        $readme = "";
	$readme = fgets ($fp,128); // Mmud id
	_karchan_log("node--karchan " . $readme);
	$readme = fgets ($fp,128); // action
	_karchan_log("node--karchan " . $readme);
	fputs ($fp, "mud\n");
	$readme = fgets ($fp,128); // name
	_karchan_log("node--karchan " . $readme);
	fputs ($fp, $name."\n");
	$readme = fgets ($fp,128); // cookie
	_karchan_log("node--karchan " . $readme);
	fputs ($fp, $password."\n");
	$readme = fgets ($fp,128); // frames
	_karchan_log("node--karchan " . $readme);
	fputs ($fp, $frames."\n");
	$readme = fgets ($fp,128); // command
	_karchan_log("node--karchan " . $readme);
	fputs ($fp, $command."\n.\n");

	// retrieve cookie that is always sent when attempting a login.
	$contents = "";
	while ((!feof($fp)) && ($readme != ".\n"))
	{
		// echo $readme;
        	_karchan_log("node--karchan " . $readme);
		$readme = fgets ($fp,128);
		$contents .= $readme;
	}
       	_karchan_log("node--karchan " . $readme);
 	fputs ($fp, "\nOk\nOk\n");
	fclose ($fp);
	if (strcasecmp(trim($command), "quit") == 0 && substr(trim($contents),0,2) == "Ok")
	{
	   // head over to the quit page.
	   drupal_goto("/node/26");
	}
	else
	{
          print str_replace  ( "game.jsp", "24" , $contents);
	}
//<div class="form-textarea-wrapper"><textarea cols="60" rows="20"></textarea></div>
  ?>

<form method="post" action="/node/24" id="CommandForm" name="CommandForm">
<?php
  if (!$bigtalk)
    {?>
    <input type="text" id="command" name="command" size="60" value="">
    <?php
    }
    else
    {?>
<script type="text/javascript" src="/sites/all/libraries/tinymce/jscripts/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript">
	tinyMCE.init({
		// General options
		mode : "textareas",
		theme : "advanced",
		plugins : "pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,wordcount,advlist,autosave",

		// Theme options
		theme_advanced_buttons1 : "save,newdocument,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,styleselect,formatselect,fontselect,fontsizeselect",
		theme_advanced_buttons2 : "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,insertdate,inserttime,preview,|,forecolor,backcolor",
		theme_advanced_buttons3 : "tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr,|,print,|,ltr,rtl,|,fullscreen",
		theme_advanced_buttons4 : "insertlayer,moveforward,movebackward,absolute,|,styleprops,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,pagebreak,restoredraft",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left",
		theme_advanced_statusbar_location : "bottom",
		theme_advanced_resizing : true
	});
</script>
<!-- /TinyMCE -->
		<!-- Gets replaced with TinyMCE, remember HTML in a textarea should be encoded -->
			<textarea id="command" name="command" rows="15" cols="80" style="width: 80%">
			</textarea>
    <?php
    }
    ?>
<input type="submit" value="Submit">
</form>

<script type="text/javascript">
function setfocus() 
{
  document.CommandForm.command.focus();
}
setfocus();
</script>
 
 
 
  <?php print render($content['comments']); ?>

</div>
