<?php
  	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());

	$result = mysql_query("INSERT INTO Category VALUES(NULL, 'yolo', 'bolo')");
	$location = mysql_insert_id();
	
	echo $result;
	echo $location;
?>