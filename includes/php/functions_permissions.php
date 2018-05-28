<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");

	function noEnoughPermissions()
	{
		error('Vous n\'avez pas les privilèges nécessaires pour accéder à ces données.');
	}

	function getGrade($groups, $rank)
	{
		return $groups[$rank]['name'];
	}

	function hasPermission($groups, $rank, $string)
	{
		return (isset($groups[$rank][$string]) AND $groups[$rank][$string]) OR $rank >= 100;
	}

	function getPermission($groups, $rank, $string, $default = false)
	{
		if (!isset($groups[$rank][$string]))
		{
			return $default;
		}
		return $groups[$rank][$string];
	}
	
	function canOverride($user, $targetUser)
	{
		if ($user == false)
		{
			return false;
		}
		if ($targetUser == false)
		{
			return true;
		}
		if (strcasecmp($user['mcname'], $targetUser['mcname']) == 0)
		{
			return true;
		}
		if ($user['rank'] > $targetUser['rank'] OR hasPermission($GLOBALS['groups'], $user['rank'], "bypass.assignlevel"))
		{
			return true;
		}
		return $targetUser['rank'] == $user['rank'] AND hasPermission($GLOBALS['groups'], $user['rank'], "bypass.level");
	}

?>