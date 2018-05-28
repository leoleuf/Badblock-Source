<?php

	define('secured', true);

	function sanitize_output($buffer) {

		$search = array(
			'/\>[^\S ]+/s',     // strip whitespaces after tags, except space
			'/[^\S ]+\</s',     // strip whitespaces before tags, except space
			'/(\s)+/s',         // shorten multiple whitespace sequences
			'/<!--(.|\s)*?-->/' // Remove HTML comments
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

	ob_start("sanitize_output");

?>
<!DOCTYPE html>
<html>
    <head>
        <title>Manager | Maintenance</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <style type="text/css">
			@font-face {
			  font-family: "San Francisco Display Ultralight";
			  font-style: normal;
			  font-weight: 400;
			  src: url(https://applesocial.s3.amazonaws.com/assets/styles/fonts/sanfrancisco/sanfranciscodisplay-ultralight-webfont.eot?#iefix) format("embedded-opentype"), url(https://applesocial.s3.amazonaws.com/assets/styles/fonts/sanfrancisco/sanfranciscodisplay-ultralight-webfont.woff2) format("woff2"), url(https://applesocial.s3.amazonaws.com/assets/styles/fonts/sanfrancisco/sanfranciscodisplay-ultralight-webfont.woff) format("woff"), url(https://applesocial.s3.amazonaws.com/assets/styles/fonts/sanfrancisco/sanfranciscodisplay-ultralight-webfont.ttf) format("truetype"), url("fonts/sanfrancisco/sanfranciscodisplay-ultralight-webfont.svg#San Francisco Display Ultralight") format("svg")
			}
            body { text-align: center; padding: 10%; background-color: #e74c3c; font-size: 20px; font-family: "San Francisco Display Ultralight"; color: #e4f1fe; }
            h1 { font-size: 50px; margin: 0; }
            article { display: block; text-align: left; max-width: 650px; margin: 0 auto; }
            a { color: #dc8100; text-decoration: none; }
            a:hover { color: #333; text-decoration: none; }
            @media only screen and (max-width : 480px) {
                h1 { font-size: 40px; }
            }
        </style>
    </head>
    <body>
        <article>
            <h2>Le Manager est temporairement en maintenance.</h2>
            <p>Nous sommes en train de mettre en production le nouveau Manager.</p>
            <p>Nous nous excusons pour tout inconvénient.</p>
            <p id="signature">&mdash; BadBlock</p>
        </article>
    </body>
</html>