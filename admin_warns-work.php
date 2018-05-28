<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);
	
	if (!hasPermission($groups, $rank, "section.administration.warns"))
	{
		header("Location: index.php");
		exit;
	}
	
	function redirectError($string)
	{
		$_SESSION['error'] = $string;
		header("Location: admin_warns.php");
		exit;
	}
	
	if (!isset($_POST))
	{
		redirectError("Veuillez remplir tous les champs du formulaire.");
	}
	
	$post = $_POST;
	
	if (!isset($post['username']) || !isset($post['message']))
	{
		redirectError('Veuillez remplir le nom et les détails de l\'avertissement.');
	}
	
	$name = secure($db, $post['username']);
	$message = secure($db, $post['message']);
	
	$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT rank, mcname FROM users WHERE mcname = '".$name."'"));
	
	if ($user == false)
	{
		redirectError('Vous ne pouvez pas ajouter d\'avertissement pour cette personne !');
	}
	
	$date = date("d/m/Y H:i:s");
	mysqli_query($db, "INSERT INTO warns(username, addedBy, date, message) VALUES('".$name."', '".secure($db, $account['mcname'])."', '".$date."', '".$message."')");
	
	// Redirect
	$_SESSION['success'] = "Avertissement ajoutée avec succès.";
	header("Location: admin_warns.php");
	exit;

?>