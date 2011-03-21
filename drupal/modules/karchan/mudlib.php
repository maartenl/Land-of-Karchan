<?php

function _karchan_log($message) 
{
  if (substr($message, 0, 13)== "node--karchan")
  {
   return;
  }
  error_log(date('d.m.Y h:i:s') . " | " . $message . "\n", 3, "/tmp/karchan-drupal-errors.log");
}

 
 function writeLogLong ($dbhandle, $arg, $addendum)
 {
   mysql_query("insert into mm_log (name, message, addendum) values(".
     "\"".quote_smart($_REQUEST{"name"})."\",\"".
     quote_smart($arg)."\",\"".
     quote_smart($addendum)
     ."\")"
     , $dbhandle);
 }
 