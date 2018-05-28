<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);
	
	if (!hasPermission($groups, $rank, "section.moderation.regroupementmaker"))
	{
		header("Location: index.php");
		exit;
	}
	
	function redirectError($string)
	{
		$_SESSION['error'] = $string;
		header("Location: mod-regroupements.php");
		exit;
	}
	
	if (!isset($_POST))
	{
		redirectError("Veuillez remplir tous les champs du formulaire.");
	}
	
	$post = $_POST;
	
	if (!isset($post['name']) || !isset($post['date']))
	{
		redirectError('Veuillez remplir le nom et la date du regroupement.');
	}
	
	$name = $post['name'];
	$date = $post['date'];
	
	$players = array();
	
	foreach ($post as $playerName => $value)
	{
		if ($playerName == "name" OR $playerName == "date")
		{
			continue;
		}
		$exists = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM users WHERE mcname = '".secure($db, $playerName)."'"));
		if ($exists != false && $exists['count'] > 0)
		{
			array_push($players, secure($db, $playerName));
		}
	}
	
	$players = json_encode($players);
	
	$date = date("d/m/Y H:i:s");
	mysqli_query($db, "INSERT INTO repartitions(name, createdBy, date, section, attendance) VALUES('".$name."', '".secure($db, $account['mcname'])."', '".$date."', 'moderation', '".$players."')");
	
	// Redirect
	$_SESSION['success'] = "Regroupement ajouté avec succès.";
	header("Location: mod-regroupements.php");
	exit;

?>