<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");

	function error($string)
	{
		echo '<div class="alert alert-danger">
		  <strong>Erreur!</strong> '.$string.'
		</div>';
	}
	
	function success($string)
	{
		echo '<div class="alert alert-success">
		  <strong>Succès!</strong> '.$string.'
		</div>';
	}
	
	function info($string)
	{
		echo '<div class="alert alert-info">
		  <strong>Information</strong> '.$string.'
		</div>';
	}
	
	function warning($string)
	{
		echo '<div class="alert alert-info">
		  <strong>Attention!</strong> '.$string.'
		</div>';
	}

?>