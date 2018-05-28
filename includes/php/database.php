<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");

	try
	{
		$db = mysqli_connect('127.0.0.1:15486', 'others', '712IYAYA8ERAr4Woq15AvijOk1S7H8');
		mysqli_select_db($db, 'others');
		mysqli_query($db, "SET NAMES UTF8");
		mysqli_set_charset($db, "utf8");
	}
	catch(Exception $e)
	{
		header("Location: database_error.php");
		exit;
	}
	
?>
