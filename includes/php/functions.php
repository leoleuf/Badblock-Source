<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");

	@session_start();

	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	error_reporting(E_ALL);
	
	date_default_timezone_set("Europe/Paris");
	require_once('database.php');
	require_once('permissions.php');
	
	require_once('google_auth.php');
	
	require_once('functions_database.php');
	require_once('functions_translation.php');
	require_once('functions_permissions.php');
	require_once('functions_login.php');
	require_once('functions_versioning.php');
	require_once('functions_minifyHtml.php');
	require_once('functions_alerts.php');
	require_once('functions_utils.php');
	
	$javaScriptFooter = '';

?>