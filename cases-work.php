<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);
	
	if (!hasPermission($groups, $rank, "section.moderation.case"))
	{
		header("Location: /");
		exit;
	}
	
	function redirectError($string)
	{
		$_SESSION['error'] = $string;
		header("Location: /cases/");
		exit;
	}
	
	$types = array(
		"verif" => "Vérification",
		"probleme" => "Problème",
		"doublecompte" => "Double Compte",
		"suspicion" => "Suspicion",
		"autre" => "Autre"
	);
	
	if (!isset($_POST))
	{
		redirectError("Veuillez remplir tous les champs du formulaire.");
	}
	
	$post = $_POST;
	
	if (!isset($post['username']) || !isset($post['message']))
	{
		redirectError('Veuillez remplir le nom et le message.');
	}
	
	$name = secure($db, $post['username']);
	$message = secure($db, $post['message']);
	$type = secure($db, $post['type']);
	
	if (!canOverride($account, $user))
	{
		redirectError('Vous n\'avez pas la permission d\'intéragir avec le casier de ce joueur !');
	}
	
	if (!isset($types[$type]))
	{
		redirectError('Type inconnu.');
	}
	
	/*
	<option value="verif">Vérification</option>
																			<option value="probleme">Problème</option>
																			<option value="doublecompte">Double Compte</option>
																			<option value="suspicion">Suspicion</option>
																			<option value="autre">Autre</option>
	*/
	
	$date = date("d/m/Y H:i:s");
	$time = time();
	$addedBy = secure($db, $account['mcname']);
	
	mysqli_query($db, "INSERT INTO sanctionInfo(username, message, addedBy, date, time, type) VALUES('".$name."', '".$message."', '".$addedBy."', '".$date."', '".time()."', '".secure($db, $types[$type])."')");
	
	// Redirect
	$_SESSION['success'] = "Cette information a été ajoutée avec succès dans le casier de ".$name.".";
	$_SESSION['last-casier'] = secure($db, $name);
	header("Location: /cases/".secure($db, $name));
	exit;

?>