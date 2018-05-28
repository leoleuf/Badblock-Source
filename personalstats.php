<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
	$javaScriptFooter .= "$(document).ready(function () {
					$.get(\"last_punishments.php\", function (result) {
						$('#last_punishments').html(result);
					});
					setInterval(function() {
						$.get(\"last_punishments.php\", function (result) {
							$('#last_punishments').html(result);
						});
					}, 10000);
				});";
	
?>

		<script src="https://code.highcharts.com/highcharts.js"></script>
		<script src="https://code.highcharts.com/highcharts-more.js"></script>
		<script src="https://code.highcharts.com/modules/exporting.js"></script>
		<script src="https://code.highcharts.com/modules/export-data.js"></script>
		<script src="https://code.highcharts.com/modules/solid-gauge.js"></script>

            <div class="content-page">
                <div class="content">
                    <div class="container-fluid">
					

						<?php
						
							if (!hasPermission($groups, $rank, "section.moderation.seehimselfstatistics"))
							{
								noEnoughPermissions();
							}
							else
							{
									
						?>
						
                        <div class="row">
                            <div class="col-sm-12">
                                <h4 class="page-title">Statistiques personnelles</h4>
                                <p class="text-muted page-title-alt"></p>
                            </div>
                        </div>
				
						<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Nombre de sanctions par jour</b></h4>
			
											<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
											
											<script>
												Highcharts.chart('container', {
													chart: {
														type: 'area'
													},
													title: {
														text: 'Statistiques des sanctions'
													},
													xAxis: {
														type: 'datetime',
														allowDecimals: false
													},
													yAxis: {
														title: {
															text: 'Nombre de sanctions'
														},
														labels: {
															formatter: function () {
																return this.value;
															}
														}
													},
													plotOptions: {
														area: {
															stacking: 'normal',
															marker: {
																enabled: false,
																symbol: 'circle',
																radius: 2,
																states: {
																	hover: {
																		enabled: true
																	}
																}
															}
														}
													},
													
														<?php
														
															$time = strtotime("first day of this month");
															
															$punishments = array(
																'unban' => array(),
																'unmute' => array(),
																'mute' => array(), 
																'ban' => array(),
																'tempban' => array(),
																'warn' => array(),
																'kick' => array()
															);
															
															$generics = array(
																'unban' => 'unban',
																'unbanip' => 'unban',
																'unmute' => 'unmute',
																'mute' => 'mute',
																'muteip' => 'mute',
																'ban' => 'ban',
																'banip' => 'ban',
																'tempban' => 'tempban',
																'btempban' => 'tempban',
																'warn' => 'warn',
																'kick' => 'kick',
																'tempbanip' => 'tempban'
															);
															
															function punishTypeToGeneric($string)
															{
																return $GLOBALS["generics"][$string];
															}
															
															$query = mysqli_query($db, "SELECT type, timestamp FROM sanctions WHERE banner = '".secure($db, $account['mcname'])."' AND timestamp >= '".($time * 1000)."'");
															
															for ($i = 0; $i <= date("d"); $i++)
															{
																$minTime = $time + (86400 * $i);
																$maxTime = $minTime + 86400;
																
																foreach ($punishments as $key => $value)
																{
																	$punishments[$key][$minTime] = 0;
																}
															}
															
															while ($data = mysqli_fetch_assoc($query))
															{
																$generic = punishTypeToGeneric($data['type']);
																$time = $data['timestamp'] / 1000;
																$minTime = 0;
																foreach($punishments as $key => $value)
																{
																	$r = PHP_INT_MAX;
																	foreach ($value as $k => $v)
																	{
																		if ($r == PHP_INT_MAX OR abs($time - $k) < abs($time - $r))
																		{
																			$r = $k;
																		}
																	}
																	$punishments[$generic][$r] = $punishments[$generic][$r] + 1;
																}
															}
															
														?>
													series: [
														{
															name: 'Débannissement',
															data: [
																<?php
																	foreach($punishments['unban'] as $key => $value)
																	{
																		echo '['.($key * 1000).', '.$value.'],';
																	}
																?>
															]
														},
														{
															name: 'Dé-baîllonnement',
															data: [
															<?php
																	foreach($punishments['unmute'] as $key => $value)
																	{
																		echo '['.($key * 1000).', '.$value.'],';
																	}
																?>
															]
														},
														{
															name: 'Baîllonnement',
															data: [
															<?php
																	foreach($punishments['mute'] as $key => $value)
																	{
																		echo '['.($key * 1000).', '.$value.'],';
																	}
																?>
															]
														},
														{
															name: 'Bannissement',
															data: [
															<?php
																	foreach($punishments['ban'] as $key => $value)
																	{
																		echo '['.($key * 1000).', '.$value.'],';
																	}
																?>
															]
														},
														{
															name: 'Bannissement temporaire',
															data: [
															<?php
																	foreach($punishments['tempban'] as $key => $value)
																	{
																		echo '['.($key * 1000).', '.$value.'],';
																	}
																?>
															]
														},
														{
															name: 'Avertissement',
															data: [
															<?php
																	foreach($punishments['warn'] as $key => $value)
																	{
																		echo '['.($key * 1000).', '.$value.'],';
																	}
																?>
															]
														},
														{
															name: 'Éjecté',
															data: [
															<?php
																	foreach($punishments['kick'] as $key => $value)
																	{
																		echo '['.($key * 1000).', '.$value.'],';
																	}
																?>
															]
														}
													]
												});
											</script>
										
										</div>
									</div>
						
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Objectif du mois</b></h4>
			
											<div style="width: 100%; height: 200px; margin: 0 auto">
												<div id="connectionTime" style="width: 25%; height: 200px; float: left"></div>
												<div id="modTime" style="width: 25%; height: 200px; float: left"></div>
												<div id="igSanctionsPercent" style="width: 25%; height: 200px; float: left"></div>
												<div id="guardianerPercent" style="width: 25%; height: 200px; float: left"></div>
											</div>

											<?php
											
												$minConnectionTime = getPermission($groups, $rank, "section.moderation.minConnectionTime", 1);
												$minModTime = getPermission($groups, $rank, "section.moderation.minModTime", 1);
												$minIGSanction = getPermission($groups, $rank, "section.moderation.minIGSanction", 1);
												$minGuardianer = getPermission($groups, $rank, "section.moderation.minGuardianer", 1);
												$minRepartitions = getPermission($groups, $rank, "section.moderation.minRepartitions", 1);
												
												$minTime = strtotime('first day of this month') * 1000;
												$timeInfo = mysqli_fetch_assoc(mysqli_query($db, "SELECT SUM(totalTime) AS totalTime, SUM(sanctionsTime) AS sanctionsTime, SUM(sanctions) AS sanctions FROM staffSessions WHERE timestamp >= '".$minTime."' && playerName = '".secure($db, $account['mcname'])."'"));
												
												$connectionTime = $timeInfo['totalTime'];
												$modTime = $timeInfo['sanctionsTime'] * 4;
												$igSanctions = $timeInfo['sanctions'];
												
												$connectionTimePercent = round($connectionTime / $minConnectionTime * 100);
												$modTimePercent = round($modTime / $minModTime * 100);
												$IGSanctionsPercent = round($igSanctions / $minIGSanction * 100);
												
												$guardianerInfo = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS total FROM reportMsg WHERE timestamp >= '".$minTime."' && playerTo = '".secure($db, $account['mcname'])."' && done = 'true'"));
												
												$guardianerPercent = round($guardianerInfo['total'] / $minGuardianer * 100);
												
											?>
											<script>
											
												var gaugeOptions = {

													chart: {
														type: 'solidgauge'
													},

													title: null,

													pane: {
														center: ['50%', '85%'],
														size: '140%',
														startAngle: -90,
														endAngle: 90,
														background: {
															backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
															innerRadius: '60%',
															outerRadius: '100%',
															shape: 'arc'
														}
													},

													tooltip: {
														enabled: false
													},

													yAxis: {
														stops: [
															[0.9, '#55BF3B'], // green
															[0.5, '#DDDF0D'], // yellow
															[0, '#DF5353'] // red
														],
														lineWidth: 0,
														minorTickInterval: null,
														tickAmount: 2,
														title: {
															y: -70
														},
														labels: {
															y: 16
														}
													},

													plotOptions: {
														solidgauge: {
															dataLabels: {
																y: 5,
																borderWidth: 0,
																useHTML: true
															}
														}
													}
												};
												
												var gaugeOptions2 = {

													chart: {
														type: 'solidgauge'
													},

													title: null,

													pane: {
														center: ['50%', '85%'],
														size: '140%',
														startAngle: -90,
														endAngle: 90,
														background: {
															backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
															innerRadius: '60%',
															outerRadius: '100%',
															shape: 'arc'
														}
													},

													tooltip: {
														enabled: false
													},

													yAxis: {
														stops: [
															[0.9, '#55BF3B'], // green
															[0.5, '#DDDF0D'], // yellow
															[0, '#DF5353'] // red
														],
														lineWidth: 0,
														minorTickInterval: null,
														tickAmount: 2,
														title: {
															y: -70
														},
														labels: {
															y: 16
														}
													},

													plotOptions: {
														solidgauge: {
															dataLabels: {
																y: 5,
																borderWidth: 0,
																useHTML: true
															}
														}
													}
												};

												var gaugeOptions3 = {

													chart: {
														type: 'solidgauge'
													},

													title: null,

													pane: {
														center: ['50%', '85%'],
														size: '140%',
														startAngle: -90,
														endAngle: 90,
														background: {
															backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
															innerRadius: '60%',
															outerRadius: '100%',
															shape: 'arc'
														}
													},

													tooltip: {
														enabled: false
													},

													yAxis: {
														stops: [
															[0.9, '#55BF3B'], // green
															[0.5, '#DDDF0D'], // yellow
															[0, '#DF5353'] // red
														],
														lineWidth: 0,
														minorTickInterval: null,
														tickAmount: 2,
														title: {
															y: -70
														},
														labels: {
															y: 16
														}
													},

													plotOptions: {
														solidgauge: {
															dataLabels: {
																y: 5,
																borderWidth: 0,
																useHTML: true
															}
														}
													}
												};

												var gaugeOptions4 = {

													chart: {
														type: 'solidgauge'
													},

													title: null,

													pane: {
														center: ['50%', '85%'],
														size: '140%',
														startAngle: -90,
														endAngle: 90,
														background: {
															backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
															innerRadius: '60%',
															outerRadius: '100%',
															shape: 'arc'
														}
													},

													tooltip: {
														enabled: false
													},

													yAxis: {
														stops: [
															[0.9, '#55BF3B'], // green
															[0.5, '#DDDF0D'], // yellow
															[0, '#DF5353'] // red
														],
														lineWidth: 0,
														minorTickInterval: null,
														tickAmount: 2,
														title: {
															y: -70
														},
														labels: {
															y: 16
														}
													},

													plotOptions: {
														solidgauge: {
															dataLabels: {
																y: 5,
																borderWidth: 0,
																useHTML: true
															}
														}
													}
												};

												var chartSpeed = Highcharts.chart('connectionTime', Highcharts.merge(gaugeOptions,
												{
													yAxis: {
														min: 0,
														max: 100,
														title: {
															text: 'Objectif - Temps de connexion'
														}
													},

													credits: {
														enabled: false
													},

													series: [{
														name: 'Objectif - Temps de connexion',
														data: [<?php echo $connectionTimePercent; ?>],
														dataLabels: {
															format: '<div style="text-align:center"><span style="font-size:25px;color:' +
																((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y}</span> %</div>'
														},
														tooltip: {
															valueSuffix: ' %'
														}
													}]

												}));

												var chartSpeed = Highcharts.chart('modTime', Highcharts.merge(gaugeOptions2,
												{
													yAxis: {
														min: 0,
														max: 100,
														title: {
															text: 'Objectif - Temps de modération'
														}
													},

													credits: {
														enabled: false
													},

													series: [{
														name: 'Objectif - Temps de modération',
														data: [<?php echo $modTimePercent; ?>],
														dataLabels: {
															format: '<div style="text-align:center"><span style="font-size:25px;color:' +
																((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y}</span> %</div>'
														},
														tooltip: {
															valueSuffix: ' %'
														}
													}]

												}));

												var chartSpeed = Highcharts.chart('igSanctionsPercent', Highcharts.merge(gaugeOptions3,
												{
													yAxis: {
														min: 0,
														max: 100,
														title: {
															text: 'Objectif - Nombre de sanctions en jeu'
														}
													},

													credits: {
														enabled: false
													},

													series: [{
														name: 'Objectif - Nombre de sanctions en jeu',
														data: [<?php echo $IGSanctionsPercent; ?>],
														dataLabels: {
															format: '<div style="text-align:center"><span style="font-size:25px;color:' +
																((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y}</span> %</div>'
														},
														tooltip: {
															valueSuffix: ' %'
														}
													}]

												}));

												var chartSpeed = Highcharts.chart('guardianerPercent', Highcharts.merge(gaugeOptions4,
												{
													yAxis: {
														min: 0,
														max: 100,
														title: {
															text: 'Objectif - Guardianer effectués'
														}
													},

													credits: {
														enabled: false
													},

													series: [{
														name: 'Objectif - Guardianer effectués',
														data: [<?php echo $guardianerPercent; ?>],
														dataLabels: {
															format: '<div style="text-align:center"><span style="font-size:25px;color:' +
																((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y}</span> %</div>'
														},
														tooltip: {
															valueSuffix: ' %'
														}
													}]

												}));

											</script>
										
										</div>
									</div>
						</div>
						
						<?php
						
							}
							
							require('includes/php/footer.php');
							
						?>