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
	
	if (!isset($post['id']))
	{
		redirectError('Veuillez préciser le regroupement.');
	}
	
	$id = $post['id'];
	$id = intval($id);
	
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
	
	mysqli_query($db, "UPDATE repartitions SET attendance = '".$players."' WHERE id = '".$id."'");
	
	// Redirect
	$_SESSION['id-edit'] = $id;
	$_SESSION['success'] = "Regroupement modifié avec succès.";
	header("Location: mod-regroupements.php?id=".$id);
	exit;

?>