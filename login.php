<?php

	define('secured', true);

	require('includes/php/functions.php');
	
	redirectIfNotLogOn($db);
	
	if (function_exists("showVersioning"))
	{
		showVersioning();
	}
	
	minify();
	
?>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Manager | Connexion</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">	
	<link rel="icon" type="image/png" href="images/icons/favicon.ico"/>
	<?php
	
	/*$css = array(
		"fonts/font-awesome-4.7.0/css/font-awesome.min.css",
		"fonts/iconic/css/material-design-iconic-font.min.css",
		"css/util.css",
		"css/main.css"
	);
	
	function d($s)
	{
		$l = explode("/", $s);
		$b = "";
		for ($i = 0; $i <= count($l) - 3; $i++)
		{
			$b .= $l[$i].'/';
		}
		return $b;
	}
		
	foreach ($css as $key)
	{
	echo '<style>';
		$content = file_get_contents($key);
		$content = str_replace("../", d($key), $content);
		$content = minifyALot($content);
		echo $content;
	echo '</style>';
	}*/
		
	?>
	<style>
	<?php
	
	$regex = array(
		"`^([\t\s]+)`ism"=>'',
		"`^\/\*(.+?)\*\/`ism"=>"",
		"`([\n\A;]+)\/\*(.+?)\*\/`ism"=>"$1",
		"`([\n\A;\s]+)//(.+?)[\n\r]`ism"=>"$1\n",
		"`(^[\r\n]*|[\r\n]+)[\s\t]*[\r\n]+`ism"=>"\n"
		);
		
	$buffer = "@font-face{font-family:'FontAwesome';src:url('fonts/font-awesome-4.7.0/fonts/fontawesome-webfont.eot');src:url('fonts/font-awesome-4.7.0/fonts/fontawesome-webfont.eot') format('embedded-opentype'),url('fonts/font-awesome-4.7.0/fonts/fontawesome-webfont.woff2') format('woff2'),url('fonts/font-awesome-4.7.0/fonts/fontawesome-webfont.woff') format('woff'),url('fonts/font-awesome-4.7.0/fonts/fontawesome-webfont.ttf') format('truetype'),url('fonts/font-awesome-4.7.0/fonts/fontawesome-webfont.svg') format('svg');font-weight:normal;font-style:normal} @font-face{font-family:Material-Design-Iconic-Font;src:url(fonts/iconic/fonts/Material-Design-Iconic-Font.woff2) format('woff2'),url(fonts/iconic/fonts/Material-Design-Iconic-Font.woff) format('woff'),url(fonts/iconic/fonts/Material-Design-Iconic-Font.ttf) format('truetype')} .p-t-8 {padding-top: 8px;} .p-t-65 {padding-top: 65px;} .p-b-31 {padding-bottom: 31px;} .p-b-49 {padding-bottom: 49px;} .p-b-54 {padding-bottom: 54px;} .p-l-55 {padding-left: 55px;} .p-r-55 {padding-right: 55px;} .m-b-23 {margin-bottom: 23px;} @font-face { font-family: Poppins-Regular; src: url('fonts/poppins/Poppins-Regular.ttf'); } @font-face { font-family: Poppins-Medium; src: url('fonts/poppins/Poppins-Medium.ttf'); } @font-face { font-family: Poppins-Bold; src: url('fonts/poppins/Poppins-Bold.ttf'); } @font-face { font-family: Poppins-SemiBold; src: url('fonts/poppins/Poppins-SemiBold.ttf'); } * { margin: 0px; padding: 0px; box-sizing: border-box; } body, html { height: 100%; font-family: Poppins-Regular, sans-serif; } a { font-family: Poppins-Regular; font-size: 14px; line-height: 1.7; color: #666666; margin: 0px; transition: all 0.4s; -webkit-transition: all 0.4s; -o-transition: all 0.4s; -moz-transition: all 0.4s; } a:focus { outline: none !important; } a:hover { text-decoration: none; color: #a64bf4; } input { outline: none; border: none; } input:focus { border-color: transparent !important; } input:focus::-webkit-input-placeholder { color:transparent; } input:focus:-moz-placeholder { color:transparent; } input:focus::-moz-placeholder { color:transparent; } input:focus:-ms-input-placeholder { color:transparent; } input::-webkit-input-placeholder { color: #adadad;} input:-moz-placeholder { color: #adadad;} input::-moz-placeholder { color: #adadad;} input:-ms-input-placeholder { color: #adadad;} button { outline: none !important; border: none; background: transparent; } button:hover { cursor: pointer; }  .limiter { width: 100%; margin: 0 auto; } .container-login100 { width: 100%; min-height: 100vh; display: -webkit-box; display: -webkit-flex; display: -moz-box; display: -ms-flexbox; display: flex; flex-wrap: wrap; justify-content: center; align-items: center; padding: 15px; background-repeat: no-repeat; background-position: center; background-size: cover; } .wrap-login100 { width: 500px; background: #fff; border-radius: 10px; overflow: hidden; }  .login100-form { width: 100%; } .login100-form-title { display: block; font-family: Poppins-Bold; font-size: 39px; color: #333333; line-height: 1.2; text-align: center; }  .wrap-input100 { width: 100%; position: relative; border-bottom: 2px solid #d9d9d9; } .label-input100 { font-family: Poppins-Regular; font-size: 14px; color: #333333; line-height: 1.5; padding-left: 7px; } .input100 { font-family: Poppins-Medium; font-size: 16px; color: #333333; line-height: 1.2; display: block; width: 100%; height: 55px; background: transparent; padding: 0 7px 0 43px; } .focus-input100 { position: absolute; display: block; width: 100%; height: 100%; top: 0; left: 0; pointer-events: none; } .focus-input100::after { content: attr(data-symbol); font-family: Material-Design-Iconic-Font; color: #adadad; font-size: 22px; display: -webkit-box; display: -webkit-flex; display: -moz-box; display: -ms-flexbox; display: flex; align-items: center; justify-content: center; position: absolute; height: calc(100% - 20px); bottom: 0; left: 0; padding-left: 13px; padding-top: 3px; } .focus-input100::before { content: ''; display: block; position: absolute; bottom: -2px; left: 0; width: 0; height: 2px; background: #7f7f7f; -webkit-transition: all 0.4s; -o-transition: all 0.4s; -moz-transition: all 0.4s; transition: all 0.4s; } .input100:focus + .focus-input100::before { width: 100%; } .input100:focus + .focus-input100::after { color: #a64bf4; } .container-login100-form-btn { display: -webkit-box; display: -webkit-flex; display: -moz-box; display: -ms-flexbox; display: flex; flex-wrap: wrap; justify-content: center; } .wrap-login100-form-btn { width: 100%; display: block; position: relative; z-index: 1; border-radius: 25px; overflow: hidden; margin: 0 auto; box-shadow: 0 5px 30px 0px rgba(3, 216, 222, 0.2); -moz-box-shadow: 0 5px 30px 0px rgba(3, 216, 222, 0.2); -webkit-box-shadow: 0 5px 30px 0px rgba(3, 216, 222, 0.2); -o-box-shadow: 0 5px 30px 0px rgba(3, 216, 222, 0.2); -ms-box-shadow: 0 5px 30px 0px rgba(3, 216, 222, 0.2); } .login100-form-bgbtn { position: absolute; z-index: -1; width: 300%; height: 100%; background: #a64bf4; background: -webkit-linear-gradient(right, #00dbde, #fc00ff, #00dbde, #fc00ff); background: -o-linear-gradient(right, #00dbde, #fc00ff, #00dbde, #fc00ff); background: -moz-linear-gradient(right, #00dbde, #fc00ff, #00dbde, #fc00ff); background: linear-gradient(right, #00dbde, #fc00ff, #00dbde, #fc00ff); top: 0; left: -100%; -webkit-transition: all 0.4s; -o-transition: all 0.4s; -moz-transition: all 0.4s; transition: all 0.4s; } .login100-form-btn { font-family: Poppins-Medium; font-size: 16px; color: #fff; line-height: 1.2; text-transform: uppercase; display: -webkit-box; display: -webkit-flex; display: -moz-box; display: -ms-flexbox; display: flex; justify-content: center; align-items: center; padding: 0 20px; width: 100%; height: 50px; } .wrap-login100-form-btn:hover .login100-form-bgbtn { left: 0; } .validate-input { position: relative; } @media (max-width: 576px) { .wrap-login100 { padding-left: 15px; padding-right: 15px; } }";
		
	$buffer = preg_replace(array_keys($regex),$regex,$buffer);
	
	echo $buffer;
	
	?>
	</style>
	
</head>
<body>
	
	<div class="limiter">
		<div class="container-login100" style="background-image: url('images/bg-01.jpg');">
			<div class="wrap-login100 p-l-55 p-r-55 p-t-65 p-b-54">
				<form method="post" action="login-work.php" class="login100-form validate-form">
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
					
					<div class="wrap-input100 validate-input m-b-23" data-validate = "Un nom d'utilisateur est nécessaire">
						<span class="label-input100">Nom d'utilisateur</span>
						<input class="input100" type="text" name="username" placeholder="Entrez votre nom d'utilisateur">
						<span class="focus-input100" data-symbol="&#xf206;"></span>
					</div>

					<div class="wrap-input100 validate-input" data-validate="Un mot de passe est nécessaire">
						<span class="label-input100">Mot de passe</span>
						<input class="input100" type="password" name="password" placeholder="Entrez votre mot de passe">
						<span class="focus-input100" data-symbol="&#xf190;"></span>
					</div>
					
					<div class="text-right p-t-8 p-b-31">
						<a href="forgot.php">
							Mot de passe oublié ?
						</a>
					</div>
					
					<div style="margin-top: 5px;"></div>
					
					<div class="container-login100-form-btn">
						<div class="wrap-login100-form-btn">
							<div class="login100-form-bgbtn"></div>
							<button class="login100-form-btn">
								Connexion
							</button>
						</div>
					</div>

				</form>
			</div>
		</div>
	</div>
	

	<div id="dropDownSelect1"></div>
	
</body>
</html>