<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
	if (!isset($removeJs))
	{
		$removeJs = true;
	}
	
?>

            <div class="content-page">
                <div class="content">
                    <div class="container-fluid">
					
                        <div class="row">
                            <div class="col-sm-12">
                                <h4 class="page-title">Tableau de bord</h4>
                                <p class="text-muted page-title-alt"></p>
                            </div>
                        </div>

						<?php

						$done = false;
						
						$warns = mysqli_fetch_assoc(mysqli_query($db, "SELECT date, message FROM warns WHERE username = '".secure($db, $account['mcname'])."' ORDER BY id DESC LIMIT 1;"));
						$warnCount = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM warns WHERE username = '".secure($db, $account['mcname'])."';"));
						
						$warnCount = min($warnCount['count'], 3);
						if ($warns != false)
						{
							error('Vous avez reçu un avertissement le '.secure($db, $warns['date']).'. Avertissement '.$warnCount.'/3<br />
							<b>Raison :</b> '.secure($db, $warns['message']));
						}
						
						if (hasPermission($groups, $rank, "section.moderation.goals"))
						{
						
									$done = true;
									$minConnectionTime = getPermission($groups, $rank, "section.moderation.minConnectionTime", 1);
									$minModTime = getPermission($groups, $rank, "section.moderation.minModTime", 1);
									$minIGSanction = getPermission($groups, $rank, "section.moderation.minIGSanction", 1);
									$minGuardianer = getPermission($groups, $rank, "section.moderation.minGuardianer", 1);
									$minRepartitions = getPermission($groups, $rank, "section.moderation.minRepartitions", 1);
									
									$minTime = strtotime('first day of this month midnight') * 1000;
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
						
                        <div class="row">
                            <div class="col-md-6 col-lg-6 col-xl-3">
                                <div class="widget-bg-color-icon card-box fadeInDown animated">
                                    <div class="bg-icon bg-icon-info pull-left">
                                        <i class="md md-equalizer text-purple"></i>
                                    </div>
                                    <div class="text-right">
                                        <h3 class="text-dark"><b class="counter"><?php echo $connectionTimePercent; ?></b> %</h3>
                                        <p class="text-muted mb-0">Objectif de connexion atteint</p>
                                    </div>
                                    <div class="clearfix"></div>
                                </div>
                            </div>

                            <div class="col-md-6 col-lg-6 col-xl-3">
                                <div class="widget-bg-color-icon card-box">
                                    <div class="bg-icon bg-icon-pink pull-left">
                                        <i class="md md-equalizer text-purple"></i>
                                    </div>
                                    <div class="text-right">
                                        <?php
											if ($minModTime > 1)
											{
										?>
										<h3 class="text-dark"><b class="counter"><?php echo $modTimePercent; ?></b> %</h3>
                                        <p class="text-muted mb-0">Objectif de modération atteint</p>
											<?php
											}
											else
											{
										?>
                                        <p class="text-muted mb-0">Pas d'objectif de temps de modération.</p>
										<?php
											}
											?>
									</div>
                                    <div class="clearfix"></div>
                                </div>
                            </div>

                            <div class="col-md-6 col-lg-6 col-xl-3">
                                <div class="widget-bg-color-icon card-box">
                                    <div class="bg-icon bg-icon-purple pull-left">
                                        <i class="md md-equalizer text-purple"></i>
                                    </div>
                                    <div class="text-right">
									<?php
										if ($minIGSanction > 1)
											{
										?>
                                        <h3 class="text-dark"><b class="counter"><?php echo $IGSanctionsPercent; ?></b> %</h3>
                                        <p class="text-muted mb-0">Objectif de sanctions atteint</p>
											<?php
											}
											else
											{
										?>
                                        <p class="text-muted mb-0">Pas d'objectif de nombre de sanctions.</p>
										<?php
											}
										?>
                                    </div>
                                    <div class="clearfix"></div>
                                </div>
                            </div>

                            <div class="col-md-6 col-lg-6 col-xl-3">
                                <div class="widget-bg-color-icon card-box">
                                    <div class="bg-icon bg-icon-success pull-left">
                                        <i class="md md-equalizer text-purple"></i>
                                    </div>
                                    <div class="text-right">
                                        <h3 class="text-dark"><b class="counter"><?php echo $guardianerPercent; ?></b> %</h3>
                                        <p class="text-muted mb-0">Objectif de Guardianer atteint</p>
                                    </div>
                                    <div class="clearfix"></div>
                                </div>
                            </div>
                        </div>
						
						<?php
						
							}
							
							if (hasPermission($groups, $rank, "section.moderation"))
							{
									
									$done = true;

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
							

                        <div class="row">
                            <div class="col-12">
                                <div class="card-box">

                                    <div class="table-rep-plugin">
                                        <div class="table-responsive" data-pattern="priority-columns" name="last_punishments" id="last_punishments">
											Chargement des dernières sanctions...
                                        </div>

                                    </div>

                                </div>
                            </div>
                        </div>
						
						<?php
						
							}
							
							if (!$done)
							{
							
						?>
						<div class="row">
                            <div class="col-12">
                                <div class="card-box">

                                    <div class="table-rep-plugin">
                                        <div class="table-responsive" data-pattern="priority-columns" name="last_punishments" id="last_punishments">
											Vous n'avez pas d'outils à votre disposition avec votre grade sur le tableau de bord.
                                        </div>

                                    </div>

                                </div>
                            </div>
                        </div>
						<?php
							
							}
							
							require('includes/php/footer.php');
						
						?>