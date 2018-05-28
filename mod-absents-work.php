<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);
	
	if (!hasPermission($groups, $rank, "section.moderation.absents"))
	{
		header("Location: index.php");
		exit;
	}
	
	function redirectError($string)
	{
		$_SESSION['error'] = $string;
		header("Location: mod-absents.php");
		exit;
	}
	
	if (!isset($_POST))
	{
		redirectError("Veuillez remplir tous les champs du formulaire.");
	}
	
	$post = $_POST;
	
	if (!isset($post['username']) || !isset($post['date']))
	{
		redirectError('Veuillez remplir le nom et la date de l\'absence.');
	}
	
	$name = secure($db, $post['username']);
	$date = secure($db, $post['date']);
	
	$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT rank, mcname FROM users WHERE (rank = 55 || rank = 50 || rank = 40 || rank = 30) && mcname = '".$name."' ORDER BY rank DESC, mcname ASC;"));
	
	if ($user == false)
	{
		redirectError('Nom d\'utilisateur inconnu.');
	}
	
	$explode = explode(" - ", $date);
	
	if (count($explode) != 2)
	{
		redirectError('Le format de la plage est inexact. Vérifiez vos informations.');
	}
	
	$firstDate = strtotime($explode[0]) + 86399;
	$endDate = strtotime($explode[1]) + 86399;
	
	$date = date("d/m/Y H:i:s");
	mysqli_query($db, "INSERT INTO absents(username, startTime, endTime, addedBy, addedTime, section) VALUES('".$name."', '".$firstDate."', '".$endDate."', '".secure($db, $account['mcname'])."', '".time()."', 'moderation')");
	
	// Redirect
	$_SESSION['success'] = "Absence ajoutée avec succès.";
	header("Location: mod-absents.php");
	exit;

?>