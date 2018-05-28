<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
	if (isset($_SESSION['error']))
	{
		$error = htmlspecialchars($_SESSION['error']);
		unset($_SESSION['error']);
		$_POST['paginationSwitch'] = "ok";
	}
	
	if (isset($_SESSION['id-edit']))
	{
		$_GET['id'] = intval($_SESSION['id-edit']);
		unset($_SESSION['id-edit']);
	}
	
?>

			<link href="plugins/bootstrap-table/css/bootstrap-table.min.css" rel="stylesheet" type="text/css" />
			<link href="plugins/bootstrap-tagsinput/css/bootstrap-tagsinput.css" rel="stylesheet" />
			<link href="plugins/switchery/css/switchery.min.css" rel="stylesheet" />
			<link href="plugins/multiselect/css/multi-select.css"  rel="stylesheet" type="text/css" />
			<link href="plugins/select2/css/select2.min.css" rel="stylesheet" type="text/css" />
			<link href="plugins/bootstrap-select/css/bootstrap-select.min.css" rel="stylesheet" />
			<link href="plugins/bootstrap-touchspin/css/jquery.bootstrap-touchspin.min.css" rel="stylesheet" />

            <div class="content-page">
                <div class="content">
                    <div class="container-fluid">
					
					<?php
					
						if (!hasPermission($groups, $rank, "section.moderation.infos"))
						{
							noEnoughPermissions();
						}
						else
						{
					
					?>
						
							<div class="row">
								<div class="col-sm-12">
									<h4 class="page-title">Classement de la modération</h4>
									<p class="text-muted page-title-alt"></p>
								</div>
							</div>

							<?php
							
									if (isset($error))
									{
										?>
										<div class="alert alert-danger">
										  <strong>Erreur!</strong> <?php echo $error; ?>
										</div>
										<?php
									}
									
							?>
							
							 <div class="row">
								<div class="col-sm-12">
									<div class="card-box">
										<h4 class="m-t-0 header-title"><b>Classement - Modération</b></h4>
										<p class="text-muted font-13">
											<i>Information : Seules les données du <?php echo date("d/m/Y", strtotime('first day of this month')); ?> au <?php echo date("d/m/Y"); ?> sont prises en compte dans la méthodologie de calcul.</i>
										</p>

										<table data-toggle="table" data-sort-name="modTime" data-sort-order="desc"
											   data-show-columns="false"
											   data-page-list="[5, 10, 20, 50, 100]"
											   data-page-size="10"
											   data-pagination="true" data-show-pagination-switch="true" class="table-bordered ">
											<thead>
											<tr>
												<th data-field="id" data-switchable="false">N°</th>
												<th data-field="mcname">Pseudonyme</th>
												<th data-field="rank">Grade</th>
												<th data-field="connectionTime">Temps de connexion</th>
												<th data-field="modTime">Temps de modération</th>
												<th data-field="guardianer">Guardianer traités</th>
												<th data-field="punishments">Nombre de sanctions</th>
												<th data-field="regroupements">Regroupements</th>
											</tr>
											</thead>

											<tbody>
											<?php
												$minTime = strtotime('first day of this month midnight') * 1000;
												$minDate = date("Y-m-01 H:i:s", $minTime);
												
												$query = mysqli_query($db, "SELECT * FROM users WHERE (rank = 50 OR rank = 40 OR rank = 55 OR rank = 100) AND disabled = '0' ORDER BY rank ASC;");
												while ($data = mysqli_fetch_assoc($query))
												{
													
													$minConnectionTime = getPermission($groups, $data['rank'], "section.moderation.minConnectionTime", 1);
													$minModTime = getPermission($groups, $data['rank'], "section.moderation.minModTime", 1);
													$minIGSanction = getPermission($groups, $data['rank'], "section.moderation.minIGSanction", 1);
													$minGuardianer = getPermission($groups, $data['rank'], "section.moderation.minGuardianer", 1);
													$minRepartitions = getPermission($groups, $data['rank'], "section.moderation.minRepartitions", 1);
													
													$timeInfo = mysqli_fetch_assoc(mysqli_query($db, "SELECT SUM(totalTime) AS totalTime, SUM(sanctionsTime) AS sanctionsTime, SUM(sanctions) AS sanctions FROM staffSessions WHERE timestamp >= '".$minTime."' && playerName = '".secure($db, $data['mcname'])."'"));
													
													$connectionTime = $timeInfo['totalTime'];
													$modTime = $timeInfo['sanctionsTime'] * 4;
													$igSanctions = $timeInfo['sanctions'];
													
													$connectionTimePercent = format($connectionTime / $minConnectionTime * 100);
													$modTimePercent = format($modTime / $minModTime * 100);
													
													if ($igSanctions == null)
													{
														$igSanctions = 0;
													}
													
													$IGSanctionsPercent = format($igSanctions / $minIGSanction * 100);
													
													$guardianerInfo = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS total FROM reportMsg WHERE timestamp >= '".$minTime."' && playerTo = '".secure($db, $data['mcname'])."' && done = 'true'"));
													
													$guardianerPercent = format($guardianerInfo['total'] / $minGuardianer * 100);
													
													$repartitionsDone = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS total FROM repartitions WHERE time >= '".$minDate."' && section = 'moderation'"));
													$repartitions = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS total FROM repartitions WHERE time >= '".$minDate."' && section = 'moderation' && attendance LIKE '%\"".secure($db, $data['mcname'])."\"%'"));
													
													if ($repartitionsDone['total'] > 0)
													{
														$repartitionRawPercent = $repartitions['total'] / $repartitionsDone['total'];
														$repartitionRawPercent = format($repartitionRawPercent * 100);
														$repartitionPercent = "<font color='".percentToColor($repartitionRawPercent)."'><b>".$repartitionRawPercent." %</b></font>";
													}
													else
													{
														$repartitionPercent = "<font color='".percentToColor(100)."'><b>100 %</b></font>";
													}
													
													?>
													<tr>
														<td><?php echo secure($db, $data['id']); ?></td>
														<td><?php echo secure($db, $data['mcname']); ?></td>
														<td><?php echo secure($db, $groups[$data['rank']]['name']); ?></td>
														<td><?php echo format_time($connectionTime); ?> / <font style="color: <?php echo percentToColor($connectionTimePercent); ?>;"><b><?php echo $connectionTimePercent; ?> %</b> </font>de l'objectif</td>
														<td data-value="<?php echo $modTime; ?>">
															<b><?php echo format_time($modTime); ?> / <font style="color: <?php echo percentToColor($modTimePercent); ?>;"><?php echo $modTimePercent; ?> % </font>de l'objectif</b>
														</td>
														<td><?php echo $guardianerInfo['total']; ?> sanctions Guardianer / <font style="color: <?php echo percentToColor($guardianerPercent); ?>;"><b><?php echo $guardianerPercent; ?> %</b> </font>de l'objectif</td>
														<td><?php echo $igSanctions; ?> sanctions en jeu / <font style="color: <?php echo percentToColor($IGSanctionsPercent); ?>;"><b><?php echo $IGSanctionsPercent; ?> %</b> </font>de l'objectif</td>
														<td><?php echo $repartitions['total']; ?>/<?php echo $repartitionsDone['total']; ?> (<?php echo $repartitionPercent; ?>)</td>
													</tr>
													<?php
												}
											?>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							
							
							 <div class="row">
								<div class="col-sm-12">
									<div class="card-box">
										<h4 class="m-t-0 header-title"><b>Classement - Helper</b></h4>
										<p class="text-muted font-13">
											<i>Information : Seules les données du <?php echo date("d/m/Y", strtotime('first day of this month')); ?> au <?php echo date("d/m/Y"); ?> sont prises en compte dans la méthodologie de calcul.</i>
										</p>

										<table data-toggle="table" data-sort-name="connectionTime" data-sort-order="desc"
											   data-show-columns="false"
											   data-page-list="[5, 10, 20, 50, 100]"
											   data-page-size="10"
											   data-pagination="true" data-show-pagination-switch="true" class="table-bordered ">
											<thead>
											<tr>
												<th data-field="id" data-switchable="false">N°</th>
												<th data-field="mcname">Pseudonyme</th>
												<th data-field="rank">Grade</th>
												<th data-field="connectionTime">Temps de connexion / Objectif %</th>
												<th data-field="guardianer">Guardianer traités / Objectif %</th>
												<th data-field="regroupements">Participation aux regroupements</th>
											</tr>
											</thead>

											<tbody>
											<?php
												$minTime = strtotime('first day of this month midnight') * 1000;
												
												$query = mysqli_query($db, "SELECT * FROM users WHERE rank = 30 AND disabled = '0' ORDER BY rank ASC;");
												while ($data = mysqli_fetch_assoc($query))
												{
													
													$minConnectionTime = getPermission($groups, $data['rank'], "section.moderation.minConnectionTime", 1);
													$minGuardianer = getPermission($groups, $data['rank'], "section.moderation.minGuardianer", 1);
													$minRepartitions = getPermission($groups, $data['rank'], "section.moderation.minRepartitions", 1);
													
													$timeInfo = mysqli_fetch_assoc(mysqli_query($db, "SELECT SUM(totalTime) AS totalTime FROM staffSessions WHERE timestamp >= '".$minTime."' && playerName = '".secure($db, $data['mcname'])."'"));
													
													$connectionTime = $timeInfo['totalTime'];
													
													$connectionTimePercent = format($connectionTime / $minConnectionTime * 100);
													
													$guardianerInfo = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS total FROM reportMsg WHERE timestamp >= '".$minTime."' && playerTo = '".secure($db, $data['mcname'])."' && done = 'true'"));
													
													$guardianerPercent = format($guardianerInfo['total'] / $minGuardianer * 100);
													
													$repartitionsDone = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS total FROM repartitions WHERE time >= '".$minDate."' && section = 'moderation'"));
													$repartitions = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS total FROM repartitions WHERE time >= '".$minDate."' && section = 'moderation' && attendance LIKE '%\"".secure($db, $data['mcname'])."\"%'"));
													
													if ($repartitionsDone['total'] > 0)
													{
														$repartitionRawPercent = $repartitions['total'] / $repartitionsDone['total'];
														$repartitionRawPercent = format($repartitionRawPercent * 100);
														$repartitionPercent = "<font color='".percentToColor($repartitionRawPercent)."'><b>".$repartitionRawPercent." %</b></font>";
													}
													else
													{
														$repartitionPercent = "<font color='".percentToColor(100)."'><b>100 %</b></font>";
													}
									
													?>
													<tr>
														<td><?php echo secure($db, $data['id']); ?></td>
														<td><?php echo secure($db, $data['mcname']); ?></td>
														<td><?php echo secure($db, $groups[$data['rank']]['name']); ?></td>
														<td><?php echo format_time($connectionTime); ?> / <font style="color: <?php echo percentToColor($connectionTimePercent); ?>;"><b><?php echo $connectionTimePercent; ?> %</b> </font>de l'objectif</td>
														<td><?php echo $guardianerInfo['total']; ?> sanctions Guardianer / <font style="color: <?php echo percentToColor($guardianerPercent); ?>;"><b><?php echo $guardianerPercent; ?> %</b> </font>de l'objectif</td>
														<td><?php echo $repartitions['total']; ?>/<?php echo $repartitionsDone['total']; ?> (<?php echo $repartitionPercent; ?>)</td>
													</tr>
													<?php
												}
											?>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<?php
							
							}
						
							require('includes/php/footer.php');
							
							?>