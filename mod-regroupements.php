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
					
						if (!hasPermission($groups, $rank, "section.moderation.regroupementmaker"))
						{
							noEnoughPermissions();
						}
						else
						{
					
					?>
						
							<div class="row">
								<div class="col-sm-12">
									<h4 class="page-title">Regroupements de la modération</h4>
									<p class="text-muted page-title-alt"></p>
								</div>
							</div>

							<?php
							
								if (isset($_POST['paginationSwitch']))
								{
									
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
											<h4 class="m-t-0 header-title"><b>Créer un regroupement</b></h4>

										<form action="mod-regroupements-work.php" method="post">
											<div class="row">
													<div class="col-md-6">
														<div class="p-20">
																<p class="mb-1"><b>Nom du regroupement</b></p>
																<p class="text-muted m-b-15 font-13">
																	Exemple: Réunion du <?php echo date("d/m/Y à H"); ?>h / Répartition du <?php echo date("d/m/Y à H"); ?>h
																</p>
																<input type="text" class="form-control" maxlength="25" name="name" id="name" />

																<div class="m-t-20">
																	<p class="mb-1"><b>Date & Heure</b></p>
																	<p class="text-muted m-b-15 font-13">
																		Saisissez la date & l'heure exacte du regroupement pour que les informations soient exactes.
																	</p>
																	<input type="text" maxlength="25" value="<?php echo date("d/m/Y H:i:s"); ?>" name="date" class="form-control" id="date" />
																</div>

																 <div style="margin-top: 20px;"></div>
																
																 <div class="form-group text-left m-b-0">
																	<button class="btn btn-primary waves-effect waves-light" style="width: 100%;" type="submit">
																		Créer
																	</button>
																</div>
														</div>
													</div>

													<div class="col-md-6">
														<div class="p-20">
															<table id="tech-companies-1" class="table  table-striped">
																<thead>
																	<tr>
																		<th>Pseudo</th>
																		<th>Grade</th>
																		<th>Présent</th>
																		<th></th>
																	</tr>
																</thead>
																<tbody>
																<?php
																
																	$query = mysqli_query($db, "SELECT mcname, rank FROM users ORDER BY rank DESC;");
																	
																	while ($data = mysqli_fetch_assoc($query))
																	{
																		$mcname = secure($db, $data['mcname']);
																		$rank = secure($db, $data['rank']);
																		if (hasPermission($groups, $rank, "section.moderation.group"))
																		{
																			$warning = '';
																			
																			$queryAbsent = mysqli_query($db, "SELECT * FROM absents WHERE username = '".$mcname."'");
																			
																			$maxMinTime = 0;
																			$missings = array();
																			
																			while ($dataAbsent = mysqli_fetch_assoc($queryAbsent))
																			{
																				if ($dataAbsent['startTime'] > $maxMinTime)
																				{
																					$maxMinTime = $dataAbsent['startTime'];
																				}
																				array_push($missings, "Absent du ".date("d/m", $dataAbsent['startTime'])." au ".date("d/m", $dataAbsent['endTime']));
																			}
																			
																			$buildMissing = "";
																			$first = true;
																			foreach ($missings as $key)
																			{
																				if ($first)
																				{
																					$first = false;
																					$buildMissing .= PHP_EOL;
																				}
																				$buildMissing .= $key;
																			}
																			
																			if ($maxMinTime >= time())
																			{
																				$warning = '<button type="button" class="btn btn-secondary" data-toggle="tooltip" data-placement="top" title="'.$buildMissing.'" style="background-color: transparent; border: none; overflow: hidden;">
																				  <img src="images/warning.png" width="32" height="32" />
																				</button>';
																			}
																			
																			echo '
																			<tr>
																				<td>'.$mcname.'</td>
																				<td>'.getGrade($groups, $rank).'</td>
																				<td><div class="switchery-demo"><input type="checkbox" data-plugin="switchery" data-color="#81c868" name="'.$mcname.'" /></div></td>
																				<td>'.$warning.'</td>
																			</tr>';
																		}
																	}
																
																?>
																</tbody>
																</table>
														</div>
													</div>
											</div>
										</div>
									</div>
								</div>
							</form>
							
							<?php
								}
								else if (isset($_GET['id']))
								{
									$id = intval($_GET['id']);
									$data = mysqli_fetch_assoc(mysqli_query($db, "SELECT * FROM repartitions WHERE id = '".$id."'"));
									if ($data == false)
									{
										error('Répartition inconnue.');
									}
									else
									{
										
										if (isset($_SESSION['success']))
										{
											success($_SESSION['success']);
										}
										
							?>
								<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Regroupement : <?php echo $data['name']; ?></b></h4>

											<form action="mod-regroupements-work-edit.php" method="post">
												<div class="row">
													<table id="tech-companies-1" class="table  table-striped">
														<thead>
															<tr>
																<th>Pseudo</th>
																<th>Grade</th>
																<th>Présent</th>
																<th></th>
															</tr>
														</thead>
														<tbody><?php
															$attendance = $data['attendance'];
															$attendance = json_decode($attendance);
															$section = $data['section'];
															$query = mysqli_query($db, "SELECT mcname, rank FROM users ORDER BY rank DESC;");
															while ($data = mysqli_fetch_assoc($query))
															{
																$mcname = secure($db, $data['mcname']);
																$rank = secure($db, $data['rank']);
																if (hasPermission($groups, $rank, "section.".$section.".group"))
																{
																	$checked = in_array($mcname, $attendance) ? " checked" : "";
																	
																	$warning = '';
																			
																	$queryAbsent = mysqli_query($db, "SELECT * FROM absents WHERE username = '".$mcname."'");
																			
																	$maxMinTime = 0;
																	$missings = array();
																	
																	while ($dataAbsent = mysqli_fetch_assoc($queryAbsent))
																	{
																		if ($dataAbsent['startTime'] > $maxMinTime)
																		{
																			$maxMinTime = $dataAbsent['startTime'];
																		}
																		array_push($missings, "Absent du ".date("d/m", $dataAbsent['startTime'])." au ".date("d/m", $dataAbsent['endTime']));
																	}
																			
																	$buildMissing = "";
																	$first = true;
																	foreach ($missings as $key)
																	{
																		if ($first)
																		{
																			$first = false;
																			$buildMissing .= PHP_EOL;
																		}
																		$buildMissing .= $key;
																	}
																			
																	if ($maxMinTime >= time())
																	{
																		$warning = '<button type="button" class="btn btn-secondary" data-toggle="tooltip" data-placement="top" title="'.$buildMissing.'" style="background-color: transparent; border: none; overflow: hidden;">
																			  <img src="images/warning.png" width="32" height="32" />
																			</button>';
																	}
																	
																	echo '
															<tr>
																<td>'.$mcname.'</td>
																<td>'.getGrade($groups, $rank).'</td>
																<td><div class="switchery-demo"><input type="checkbox" data-plugin="switchery" data-color="#81c868" name="'.$mcname.'"'.$checked.' /></div></td>
																<td>'.$warning.'</td>
															</tr>';
																}
															}
																	
														?>
														</tbody>
													</table>
												</div>
												<div class="form-group text-left m-b-0">
													<input type="input" name="id" value="<?php echo intval($_GET['id']); ?>" style="display: none;" />
													<button class="btn btn-primary waves-effect waves-light" name="edit" style="width: 100%;" type="submit">
														Modifier
													</button>
												</div>
											</form>
										</div>
									</div>
								</div>
						</div>
								</div>
							<?php
									}
								}
								else
								{
							
									if (isset($_SESSION['success']))
									{
										?>
										<div class="alert alert-success">
										  <strong>Succès!</strong> <?php echo htmlspecialchars($_SESSION['success']); ?>
										</div>
										<?php
										unset($_SESSION['success']);
									}
							
							?>
							 <div class="row">
								<div class="col-sm-12">
									<div class="card-box">
										<h4 class="m-t-0 header-title"><b>Regroupements de la modération</b> <div class="columns columns-right btn-group pull-right"><form method="post"><button class="btn btn-default" type="submit" name="paginationSwitch" aria-label="pagination Switch" title="Créer un regroupement">+</button></form></div></h4>
										<p class="text-muted font-13">
											Les réunions ainsi que les répartitions (et les regroupements en général) sont notés ici, avec leurs détails.
										</p>

										<table data-toggle="table"
											   data-show-columns="false"
											   data-page-list="[5, 10, 20]"
											   data-page-size="5"
											   data-pagination="true" data-show-pagination-switch="true" class="table-bordered ">
											<thead>
											<tr>
												<th data-field="name" data-switchable="false">Nom du regroupement</th>
												<th data-field="by">Regroupement créé par</th>
												<th data-field="date">Date</th>
												<th data-field="amount">Présence</th>
												<th data-field="details" class="text-center">Détails</th>
											</tr>
											</thead>

											<tbody>
											<?php
												$query = mysqli_query($db, "SELECT * FROM repartitions WHERE section = 'moderation' ORDER BY id DESC;");
												while ($data = mysqli_fetch_assoc($query))
												{
													?>
													<tr>
														<td><?php echo secure($db, $data['name']); ?></td>
														<td><?php echo secure($db, $data['createdBy']); ?></td>
														<td><?php echo secure($db, $data['date']); ?></td>
														<td><?php
															$attendance = $data['attendance'];
															$attendance = json_decode($attendance);
															$i = 0;
															foreach ($attendance as $key => $value)
															{
																$i++;
															}
															
															$total = 0;
															$q = mysqli_query($db, "SELECT mcname, rank FROM users ORDER BY rank DESC;");
															while ($dat2 = mysqli_fetch_assoc($q))
															{
																$mcname = secure($db, $dat2['mcname']);
																$rank = secure($db, $dat2['rank']);
																if (hasPermission($groups, $rank, "section.".$data['section'].".group"))
																{
																	$total++;
																}
															}
															echo $i.'/'.$total;
														?></td>
														<td><a href="?id=<?php echo secure($db, $data['id']); ?>">Détails</a></span></td>
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
							}
						
							require('includes/php/footer.php');
							
							?>