<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");
	
	function secure($db, $string)
	{
			return htmlspecialchars(mysqli_real_escape_string($db, $string));
	}
	
	// POST secure
	foreach ($_POST as $key => $value)
	{
		$_POST[$key] = secure($db, $value);
	}
	
?>