<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");

	$punishTypes = array(
		'unban' => 'Débannissement',
		'unbanip' => 'Débannissement IP',
		'unmute' => 'Débaîllonnement',
		'mute' => 'Baîllonnement',
		'muteip' => 'Baîllonnement IP',
		'ban' => 'Bannissement',
		'banip' => 'Bannissement IP',
		'tempban' => 'Bannissement temporaire',
		'btempban' => 'Bannisesment temporaire Pseudo/IP',
		'warn' => 'Avertissement',
		'kick' => 'Éjecté',
		'tempbanip' => 'Bannissement temporaire IP',
		'report' => 'Signalement automatique'
	);
	
	$punishColors = array(
		'unban' => 'success',
		'unbanip' => 'success',
		'unmute' => 'success',
		'mute' => 'warning',
		'kick' => 'info',
		'report' => 'info',
		'btempban' => 'danger',
		'warn' => 'warn',
		'mute' => 'primary',
		'muteip' => 'primary',
		'ban' => 'danger',
		'banip' => 'danger',
		'tempban' => 'danger',
		'tempbanip' => 'danger'
	);
	
	function percentToColor($percent)
	{
		if ($percent >= 100)
		{
			return "#27ae60";
		}
		else if ($percent >= 90)
		{
			return "#2ecc71";
		}
		else if ($percent >= 80)
		{
			return "#16a085";
		}
		else if ($percent >= 70)
		{
			return "#1abc9c";
		}
		else if ($percent >= 60)
		{
			return "#3498db";
		}
		else if ($percent >= 50)
		{
			return "#f1c40f";
		}
		else if ($percent >= 40)
		{
			return "#f39c12";
		}
		else if ($percent >= 30)
		{
			return "#e67e22";
		}
		else if ($percent >= 20)
		{
			return "#d35400";
		}
		else if ($percent >= 10)
		{
			return "#e74c3c";
		}
		else
		{
			return "#c0392b";
		}
	}
	
	function translateReason($string)
	{
		return make_links_clickable(preg_replace("(((?:&|§)[0-9A-FK-ORa-fk-or])+)", "", $string));
	}

	function translatePunishType($punishType)
	{
		return $GLOBALS['punishTypes'][$punishType];
	}
	
	function translatePunishColor($punishType)
	{
		return $GLOBALS['punishColors'][$punishType];
	}
	
	function make_links_clickable($text)
	{
		return preg_replace('!(((f|ht)tp(s)?://)[-a-zA-Zа-яА-Я()0-9@:%_+.~#?&;//=]+)!i', '<a href="$1" target="_blank">Lien</a>', $text);
	}

	function make_links_clickable_proof($text)
	{
		return preg_replace('!(((f|ht)tp(s)?://)[-a-zA-Zа-яА-Я()0-9@:%_+.~#?&;//=]+)!i', '<a href="$1" target="_blank">Lien</a><br/>', $text);
	}

	function convertToUTF8($text)
	{
		return iconv(mb_detect_encoding($text, mb_detect_order(), true), "UTF-8", $text);
	}
	
	function format($number)
	{
		return number_format($number, 2, ',' , ' ');
	}
	
	function formatInt($number)
	{
		return number_format($number, 0, ',' , ' ');
	}
	
	function format_time($t,$f=':')
	{
	  return sprintf("%02d%s%02d%s%02d%s", floor($t/3600), "h ", ($t/60)%60, "m ", $t%60, "s ");
	}

?>