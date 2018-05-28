<?php

	define('secured', true);

	require_once('includes/php/functions.php');

	redirectIfNotLogOn($db);
	
	// Nothing post
	if (!isset($_POST))
	{
		loginError("Veuillez envoyer toutes les données demandées.");
	}
	
	$post = $_POST;

	// No username or password sent
	if (!isset($post['username']) || !isset($post['password']))
	{
		loginError("Veuillez insérer votre nom d'utilisateur et votre mot de passe.");
	}

	$username = $post['username'];
	$password  = $post['password'];
	
	$data = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM users WHERE mcname = '".$username."'"));
	
	if ($data === false || $data['count'] < 1)
	{
		loginError("Ce nom d'utilisateur n'existe pas.");
	}
	
	$data = mysqli_fetch_assoc(mysqli_query($db, "SELECT mcname, password, email FROM users WHERE mcname = '".$username."'"));
	
	if ($data === false)
	{
		loginError("Ce nom d'utilisateur n'existe pas.");
	}
	
	$time = time();
	
	mysqli_query($db, "UPDATE users SET lastLoginTry = '".secure($db, $time + 60)."' WHERE mcname = '".$username."'");
	
	$password = encode($password, $data['email']);
	$enteredPassword = $data['password'];
	
	if (strcasecmp($enteredPassword, $password) !== 0)
	{
		loginError("Mot de passe invalide.");
	}
	
	/*$publicKey = "6LdoZigTAAAAAAFu5cHgIOxh2B8RnMfESWf2H5pq";
	$privateKey = "6LdoZigTAAAAAFs-7CIDG4EUggHSPxdG5Ck7y-Li";
	
	$response = file_get_contents("https://www.google.com/recaptcha/api/siteverify?secret=".secure($db, $privateKey)."&response=".secure($db, $captcha)."&remoteip=".secure($db, $_SERVER['REMOTE_ADDR']));
	$obj = json_decode($response); 
	
	if (!($obj->{'success'} == true))
	{
		loginError("Captcha invalide.");
	}*/
	
	$lastLoginTry = $data['lastLoginTry'];
	
	if ($lastLoginTry > $time)
	{
		loginError("Veuillez patienter avant de vous reconnecter.");
	}
	
	if ($data['disabled'] != 0)
	{
		loginError("Votre compte a été supprimé.");
	}
	
	$currentDate = date("d/m/Y H:i:s");
	mysqli_query($db, "INSERT INTO managerLogs(mcname, ip, date, timestamp) VALUES('".secure($db, $data['mcname'])."', '".secure($db, $_SERVER['REMOTE_ADDR'])."', '".$currentDate."', '".$time."')");
	$_SESSION['user'] = $data['mcname'];
	$_SESSION['login'] = $password;
	$_SESSION['token'] = substr(encode(rand(1, 99999), $_SESSION['login']), 0, 64);
	
	// Double authentification
	header("Location: login-doubleauth.php");
	
?>