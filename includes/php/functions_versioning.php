<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");

	function base64($length = 10)
	{
		$characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
		$charactersLength = strlen($characters);
		$randomString = '';
		for ($i = 0; $i < $length; $i++) {
			$randomString .= $characters[rand(0, $charactersLength - 1)];
		}
		return $randomString;
	}

	function showVersioning()
	{
		
		$hostname = rand(1, 17);
		$hostname = ($hostname < 10) ? "0".$hostname : $hostname;
		$cluster = rand(1, 9);
		$hostname = "cluster".$cluster."-226-".$hostname.".par10s-eu.web.front";
		
		echo "<!--
		
	  ____            _ ____  _            _        __  __                                   
	 | __ )  __ _  __| | __ )| | ___   ___| | __   |  \/  | __ _ _ __   __ _  __ _  ___ _ __ 
	 |  _ \ / _` |/ _` |  _ \| |/ _ \ / __| |/ /   | |\/| |/ _` | '_ \ / _` |/ _` |/ _ \ '__|
	 | |_) | (_| | (_| | |_) | | (_) | (__|   <    | |  | | (_| | | | | (_| | (_| |  __/ |   
	 |____/ \__,_|\__,_|____/|_|\___/ \___|_|\_\   |_|  |_|\__,_|_| |_|\__,_|\__, |\___|_|   
																			 |___/           
																		  
		Script info: script: node, date: ".date("m-d-Y H:i:s").", country: FR, language: fr
		hostname : ".$hostname."
		rlogid : ".base64(32)."=
		
!-->
";

	}

?>