<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");

	function minifyALot($string)
	{
		return trim(preg_replace('/\s\s+/', ' ', $string));
	}

	function sanitize_output($buffer)
	{

		$search = array(
			'/\>[^\S ]+/s',
			'/[^\S ]+\</s',
			'/(\s)+/s',
			'/<!--(.|\s)*?-->/'
		);

		$replace = array(
			'>',
			'<',
			'\\1',
			''
		);

		$buffer = preg_replace($search, $replace, $buffer);
		
		return $buffer;
	}

	function minify()
	{
		ob_start("sanitize_output");
	}

?>