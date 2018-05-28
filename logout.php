<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);
	
	if (!isset($_GET['token']) OR $_GET['token'] != $_SESSION['token'])
	{
		exit('Token invalide.');
	}
	
	@session_unset($_SESSION['login']);
	@session_unset($_SESSION['secret']);
	@session_unset($_SESSION['logged']);
	@session_unset($_SESSION['google']);
	@session_unset($_SESSION['token']);
	
	session_destroy();
	
	header("Location: index.php");
	exit();
	
?>