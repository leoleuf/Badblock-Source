<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");
	
	function isLogin($db)
	{
		if (!isset($_SESSION['user']) || !isset($_SESSION['login']) || !isset($_SESSION['token']))
		{
			return false;
		}
		
		$user = secure($db, $_SESSION['user']);
		$login = secure($db, $_SESSION['login']);
		
		$data = mysqli_fetch_assoc(mysqli_query($db, "SELECT password FROM users WHERE mcname = '".$user."'"));
		if ($data == false)
		{
			return false;
		}
	
		if ($data['password'] != $login)
		{
			return false;
		}
		
		return true;
	}
	
	function redirectIfNotLogOn($db)
	{
		if (isLogin($db))
		{
			header("Location: index.php");
			exit;
		}
	}
	
	function redirectIfLogOn($db, $doubleAuthPage = false)
	{
		if (!isLogin($db))
		{
			header("Location: login.php");
			exit;
		}
		if (!$doubleAuthPage)
		{
			redirectIfSecretNotValid();
		}
	}
	
	function generateTempToken()
	{
		$tempToken = encode(sha1(rand(1, 99999)), base64_encode(time()));
		mysqli_query($GLOBALS['db'], "UPDATE users SET tempToken = '".$tempToken."' WHERE mcname = '".secure($GLOBALS['db'], $GLOBALS['account']['mcname'])."'");
		return $tempToken;
	}
	
	function isSecretValid()
	{
		$users = mysqli_fetch_assoc(mysqli_query($GLOBALS['db'], "SELECT tempToken FROM users WHERE mcname = '".secure($GLOBALS['db'], $_SESSION['user'])."'"));
		return isset($_SESSION['tempToken']) && $users != false && $users['tempToken'] == $_SESSION['tempToken'];
	}
	
	function redirectIfSecretValid()
	{
		if (isSecretValid())
		{
			header("Location: index.php");
			exit;
		}
	}
	
	function redirectIfSecretNotValid()
	{
		if (!isSecretValid())
		{
			header("Location: login-doubleauth.php");
			exit;
		}
	}
	
	function loginError($string)
	{
		$_SESSION['error'] = $string;
		header("Location: login.php");
		exit;
	}
	
	function aes256_cbc_encrypt($key, $data, $iv)
	{
		if(32 !== strlen($key)) $key = hash('SHA256', $key, true);
		if(16 !== strlen($iv)) $iv = hash('MD5', $iv, true);
		$padding = 16 - (strlen($data) % 16);
		$data .= str_repeat(chr($padding), $padding);
		return mcrypt_encrypt(MCRYPT_RIJNDAEL_128, $key, $data, MCRYPT_MODE_CBC, $iv);
	}

	function encode($string, $key)
	{
		$petachunk = base64_encode(base64_encode(openssl_digest(sha1(md5($key)), 'sha512')).openssl_digest(sha1(md5(sha1(openssl_digest(md5($string), 'sha512')))), 'sha512').openssl_digest(sha1(md5(strlen($string))), 'sha512').sha1(strlen($string)).sha1(md5(sha1(md5(openssl_digest(sha1($key), 'sha512'))))).md5(openssl_digest(sha1($string), 'sha512')).sha1(md5(sha1(openssl_digest(sha1(md5(sha1($string))), 'sha512'))))).base64_encode(sha1(md5(sha1(sha1(md5(md5(strlen($string)))))))).base64_encode(sha1(md5(sha1(sha1(md5(strlen($string))))))).md5(strlen($key)).base64_encode(md5(md5(sha1(sha1(md5(md5(strlen($string)))))))).base64_encode(sha1(sha1(sha1(sha1(sha1(sha1(strlen($string)))))))).openssl_digest($string, 'sha512').base64_encode(openssl_digest($string, 'sha512')).base64_encode(openssl_digest($key, 'sha512'));
		return $petachunk.base64_decode($petachunk);
	}
	
	if (isLogin($db))
	{
		$account = mysqli_fetch_assoc(mysqli_query($db, "SELECT * FROM users WHERE mcname = '".secure($db, $_SESSION['user'])."'"));
		$rank = $account['rank'];
	}
	
	function hasSecret()
	{
		return !empty($GLOBALS['account']['secret']);
	}
	
	function hasToChoseSecret()
	{
		return !empty($GLOBALS['account']['toChoseSecret']);
	}
	
	
	function hasOnlyGeneratedSecret()
	{
		return !hasSecret() && hasToChoseSecret();
	}
	
	function getToChoseSecret()
	{
		return $GLOBALS['account']['toChoseSecret'];
	}
	
	
	function getSecret()
	{
		return $GLOBALS['account']['secret'];
	}
	
?>