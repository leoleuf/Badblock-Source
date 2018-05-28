<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");
	
	try
	{
		$guardianDb = mysqli_connect('127.0.0.1:15486', 'guardian', '2LyTs9gRKC7EsJYvyqpNdP9dGYxx5EG2fCRrBMdw5sThcUyauNVwzD2UsZu725rk');
		mysqli_select_db($guardianDb, 'guardian');
		mysqli_query($guardianDb, "SET NAMES UTF8");
		mysqli_set_charset($guardianDb, "utf8");
	}
	catch(Exception $e)
	{
		header("Location: database_error.php");
		exit;
	}
	
?>
