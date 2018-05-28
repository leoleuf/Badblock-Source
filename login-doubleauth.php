<?php

	define('secured', true);

	require('includes/php/functions.php');
	
	redirectIfLogOn($db, true);
	
	redirectIfSecretValid();
	
	$googleAuthenticator = new PHPGangsta_GoogleAuthenticator();
	
	if (hasOnlyGeneratedSecret() && isset($_POST['password']))
	{
		$testCode = secure($db, $_POST['password']);
		$checkResult = $googleAuthenticator->verifycode(getToChoseSecret(), $testCode, 2);
		if ($checkResult)
		{
			mysqli_query($db, "UPDATE users SET secret = '".secure($db, getToChoseSecret())."', toChoseSecret = '' WHERE mcname = '".secure($db, $account['mcname'])."'");
			$_SESSION['tempToken'] = generateTempToken();
			header("Location: index.php");
			exit;
		}
		else
		{
			$_SESSION['error'] = "Le code généré n'est pas le bon. Veuillez vérifier.";
		}
	}
	else if (hasSecret() && !hasToChoseSecret() && isset($_POST['password']))
	{
		$testCode = secure($db, $_POST['password']);
		$checkResult = $googleAuthenticator->verifycode(getSecret(), $testCode, 2);
		if ($checkResult)
		{
			$_SESSION['tempToken'] = generateTempToken();
			header("Location: index.php");
			exit;
		}
		else
		{
			$_SESSION['error'] = "Le code d'auth unique n'est pas le bon. Veuillez vérifier.";
		}
	}
	
?>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Manager | Double Authentification</title>
	<meta name="description" content="Le Manager de BadBlock permet d'administrer les sections du serveur, de modérer ainsi que de remplir des tâches quotidiennes en relation à la vie pratique de BadBlock.">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">	
	<link rel="icon" type="image/png" href="images/icons/favicon.ico"/>
	<link rel="stylesheet" type="text/css" href="vendor/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="fonts/font-awesome-4.7.0/css/font-awesome.min.css">
	<link rel="stylesheet" type="text/css" href="fonts/iconic/css/material-design-iconic-font.min.css">
	<link rel="stylesheet" type="text/css" href="vendor/animate/animate.css">
	<link rel="stylesheet" type="text/css" href="vendor/css-hamburgers/hamburgers.min.css">
	<link rel="stylesheet" type="text/css" href="vendor/animsition/css/animsition.min.css">
	<link rel="stylesheet" type="text/css" href="vendor/select2/select2.min.css">
	<link rel="stylesheet" type="text/css" href="vendor/daterangepicker/daterangepicker.css">
	<link rel="stylesheet" type="text/css" href="css/util.css">
	<link rel="stylesheet" type="text/css" href="css/main.css">
	
</head>
<body>
	
	<div class="limiter">
		<div class="container-login100" style="background-image: url('images/bg-01.jpg');">
			<div class="wrap-login100 p-l-55 p-r-55 p-t-65 p-b-54">
				<form method="post" action="login-doubleauth.php" class="login100-form validate-form">
					<span class="login100-form-title p-b-49">
						Manager
					</span>

					<?php
						if (isset($_SESSION['error']))
						{
							?>
							<div class="alert alert-danger">
							  <strong>Erreur</strong> <?php echo htmlspecialchars($_SESSION['error']); ?>
							</div>
							<?php
							unset($_SESSION['error']);
						}
					?>
					
					<?php
					
						if (!hasSecret())
						{
					
							if (!hasToChoseSecret())
							{
								$secret = $googleAuthenticator->createSecret();
								mysqli_query($db, "UPDATE users SET toChoseSecret = '".secure($db, $secret)."' WHERE mcname = '".secure($db, $account['mcname'])."'");
							}
							
							$secret = secure($db, getToChoseSecret());
							
					?>
							<div style="font-size: 24px; text-align: center;">
								Initialisation de la double auth
							</div>
							<br />
							
							Une clé Google Authenticator a été générée.<br />
							Clé : <b><?php echo $secret; ?></b>
							<br/>
							(cette clé doit rester absolument secrète)
							<br /><br />
							Vous pouvez l'entrer en utilisant le QRCode :
							<br />
					<?php
					
							$qrCodeUrl = $googleAuthenticator->getQRCodeGoogleUrl('BadBlock ('.secure($db, $account['mcname']).') - Manager', $secret);
							
					?>
					
							<center>
								<img src="<?php echo $qrCodeUrl; ?>" />
							</center>
							<br />
							Une fois que vous l'aurez rentré sur l'application <b>Google Authenticator</b>, vous devrez rentrer le code affiché afin de poursuivre.
							<br /><br />
							<div class="wrap-input100 validate-input m-b-23" data-validate = "Veuillez entrer le code d'essai pour vérifier votre double authentification.">
								<span class="label-input100">Code d'authentification unique</span>
								<input class="input100" type="text" name="password" placeholder="Entrez le code d'essai obtenu">
								<span class="focus-input100" data-symbol="&#xf206;"></span>
							</div>
							<br />
							
							<div class="container-login100-form-btn">
								<div class="wrap-login100-form-btn">
									<div class="login100-form-bgbtn"></div>
									<button class="login100-form-btn">
										Vérifier le code
									</button>
								</div>
							</div>
					
					<?php
					
						}
						else
						{
							
					?>
						
						<h3>Entrez votre code Google Authenticator.</h3>

						<br /><br />
						<div class="wrap-input100 validate-input m-b-23" data-validate = "Un code d'authentification est nécessaire">
							<span class="label-input100">Code d'authentification unique</span>
							<input class="input100" type="text" name="password" placeholder="Entrez votre code d'auth unique">
							<span class="focus-input100" data-symbol="&#xf206;"></span>
						</div>
						<br />
						<div class="container-login100-form-btn">
							<div class="wrap-login100-form-btn">
								<div class="login100-form-bgbtn"></div>
								<button class="login100-form-btn">
									Vérifier mon identité
								</button>
							</div>
						</div>
						
					<?php
					
						}
						
					?>
					
					<div style="margin-top: 5px;"></div>

				</form>
			</div>
		</div>
	</div>
	

	<div id="dropDownSelect1"></div>
	
	<script src="vendor/jquery/jquery-3.2.1.min.js"></script>
	<script src="vendor/animsition/js/animsition.min.js"></script>
	<script src="vendor/bootstrap/js/popper.js"></script>
	<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
	<script src="vendor/select2/select2.min.js"></script>
	<script src="vendor/daterangepicker/moment.min.js"></script>
	<script src="vendor/daterangepicker/daterangepicker.js"></script>
	<script src="vendor/countdowntime/countdowntime.js"></script>
	<script src="js/main.js"></script>

</body>
</html>