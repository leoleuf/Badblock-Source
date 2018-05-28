<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
	$dataByPage = 15;
	
	// temp redirect
	if (isset($_SESSION['last-casier']))
	{
		$_GET['username'] = secure($db, $_SESSION['last-casier']);
		unset($_SESSION['last-casier']);
	}
	
	$infoTypes = array(
		'Vérification' => 'success',
		'Problème' => 'danger',
		'Double Compte' => 'info',
		'Suspicion' => 'warning',
		'Autre' => 'warn'
	);
	
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
					
						
					  if (!hasPermission($groups, $rank, "section.moderation.case"))
					  {
						noEnoughPermissions();
					  }
					  else
					  {
						  
						  if (isset($_SESSION['error']))
						  {
					?>
						<div class="alert alert-danger">
						  <strong>Erreur!</strong> <?php echo $_SESSION['error']; ?>
						</div>
					<?php
							unset($_SESSION['error']);
					
						  }
							
						if (isset($_SESSION['success']))
						{
							success(secure($db, $_SESSION['success']));
							unset($_SESSION['success']);
						}
					
					
						if (!isset($_GET['username']) || (isset($_GET['username']) && empty(trim($_GET['username']))))
						{
					
					?>
					
					<div class="row">
								<div class="col-sm-12">
									<div class="card-box">
										<h4 class="m-t-0 header-title"><b>Rechercher le casier d'un utilisateur</b></h4>

										<form action="/cases/" method="get">
											<div class="row">
													<div class="p-20">
														<p class="mb-1"><b>Nom d'utilisateur</b></p>
														<input type="text" class="form-control" maxlength="25" name="username" id="username" />
														
														<div style="margin-top: 20px;"></div>
																
														<div class="form-group text-left m-b-0">
															<button class="btn btn-primary waves-effect waves-light" style="width: 100%;" type="submit">
																Rechercher
															</button>
														</div>
													</div>
											</div>
										</form>
									
									</div>
								</div>
					</div>
					
					<?php
					
						}
						else
						{
							
							$username = secure($db, $_GET['username']);
							$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT mcname, rank FROM users WHERE mcname = '".$username."'"));
							
							if (!canOverride($account, $user))
							{
								noEnoughPermissions();
							}
							else
							{
							
								$count = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM sanctions WHERE pseudo = '".$username."';"));
								$count = $count['count'];
					
					?>
					
							<div class="row">
								<div class="col-sm-12">
									<h4 class="page-title">Sanctions de <?php echo $username; ?> (<?php echo formatInt($count); ?>)</h4>
									<p class="text-muted page-title-alt"></p>
								</div>
							</div>
							
							<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
						
											<table id="tech-companies-1" class="table  table-striped">
												<thead>
													<tr>
														<th>Pseudo</th>
														<th>Date</th>
														<th>Type</th>
														<th>Par</th>
														<th>Temps</th>
														<th>Raison</th>
														<th>Preuve(s)</th>
													</tr>
												</thead>
												<tbody>
											
												<?php

													$pageNumber = ceil($count / $dataByPage);
													
													if (isset($_GET['page']))
													{
														$page = intval($_GET['page']);
														
														if ($page > $pageNumber)
														{
															$page = $pageNumber;
														}
														else if ($page < 1)
														{
															$page = 1;
														}
														
													}
													else
													{
														$page = 1;
													}
													
													$firstEntry = ($page - 1) * $dataByPage;
												
													$request = mysqli_query($db, "SELECT type, id, proof, timestamp, expire, banner, reason, pseudo, date FROM sanctions WHERE pseudo = '".$username."' ORDER BY id DESC LIMIT ".$firstEntry.", ".$dataByPage.";");
														
													while ($data = mysqli_fetch_assoc($request))
													{
														
														$punishType = translatePunishType($data['type']);
														$punishColor = translatePunishColor($data['type']);

														// Punishment proofs?
														if ($data['proof'] != "")
														{
															$proof = make_links_clickable_proof(strip_tags(utf8_decode($data['proof']))).' — <a href="proof.php?id='.$data['id'].'">Modifier</a>';
														}
														else
														{
															$proof = "— <a href=\"proof.php?id=".$data['id']."\">Ajouter une preuve</a>";
														}
														
														$time =  ($data['expire'] - $data['timestamp']) / 1000;
														$days = floor($time / (60 * 60 * 24));
														$time -= $days * (60 * 60 * 24);
														$hours = floor($time / (60 * 60));
														$time -= $hours * (60 * 60);
														$minutes = floor($time / 60);
														$time -= $minutes * 60;
														$seconds = floor($time);
														$time -= $seconds;

														// Warn expiration
														if ($data['type'] == "warn")
														{
															$data['expire'] = -1;
														}
														
														// Time formatter
														if ($data['expire'] != -1)
														{
															$time = ($days > 0 ? $days."j" : "").($hours > 0 ? $hours."h" : "").($minutes > 0 ? $minutes."m" : "").($seconds > 0 ? $seconds."s" : "");
														}else if ($data['type'] == "ban" OR $data['type'] == "banip")
														{
															$time = "Définitif";
														}
														else
														{
															$time = "—";
														}

														$link = "https://manager.badblock.fr/modo_logs.php?username=".secure($db, $data['banner']);
														$banner = secure($db, $data['banner']);
														if (hasPermission($groups, $rank, "section.moderation.bymoderator"))
														{
															$banner = '<a href="https://manager.badblock.fr/modo_logs.php?username='.$banner.'">'.$banner.'</a>';
														}
														
														$reason = make_links_clickable(preg_replace("(((?:&|§)[0-9A-FK-ORa-fk-or])+)", "", $data['reason']));
														
												?>
													<tr>
														<td><a href="https://manager.badblock.fr/cases/<?php echo secure($db, $data['pseudo']); ?>"><?php echo secure($db, $data['pseudo']); ?></a></td>
														<td><?php echo secure($db, $data['date']); ?></td>
														<td><span class="label label-<?php echo $punishColor; ?>"><?php echo $punishType; ?></span></td>
														<td><?php echo $banner; ?></td>
														<td><?php echo $time; ?></td>
														<td><?php echo $reason; ?></td>
														<td><?php echo $proof; ?></td>
													</tr>
											<?php
												
												}
												
											?>
												</tbody>
											</table>
											<form method="get" class="input-group" style="float:right;">
												<input type="input" name="username" value="<?php echo $username; ?>" style="display: none;" />
												  <?php
													for ($i = 1; $i <= $pageNumber; $i++)
													{
														if (abs($page - $i) <= 1 || $i == 1 || $i == $pageNumber)
														{
															echo '<input type="submit" class="btn btn-sm btn-default" value="'.$i.'" name="page" />&nbsp;&nbsp;&nbsp;';
														}
													}
												  ?>
											</form>
											<br />
									</div>
								</div>
							</div>
							
							<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Informations relatifs au casier de <?php echo $username; ?></b></h4>

										<form action="cases-work.php" method="post">
											<div class="row">
													<div class="col-md-12">
														<div class="p-20">
															<table id="tech-companies-1" class="table  table-striped">
																<thead>
																	<tr>
																		<th style="width: 10%;">Ajouté par</th>
																		<th style="width: 15%;">Date</th>
																		<th style="width: 10%;">Type</th>
																		<th>Message</th>
																	</tr>
																</thead>
																<tbody>
																<?php
																
																	$query = mysqli_query($db, "SELECT username, message, addedBy, date, type FROM sanctionInfo WHERE username = '".secure($db, $username)."' ORDER BY id DESC;");
																	
																	while ($data = mysqli_fetch_assoc($query))
																	{
																		echo '
																			<tr>
																				<td>'.secure($db, $data['username']).'</td>
																				<td>'.secure($db, $data['date']).'</td>
																				<td><span class="label label-'.$infoTypes[secure($db, $data['type'])].'">'.secure($db, $data['type']).'</span></td>
																				<td>'.secure($db, $data['message']).'</td>
																			</tr>';
																	}
																
																?>
																</tbody>
																</table>
														</div>
													</div>
													
													<div class="col-md-12">
														<div class="p-20">
																<p class="mb-1"><b>Message</b></p>
																<input type="text" class="form-control" maxlength="255" name="message" id="message" />
																<input type="text" name="username" value="<?php echo $username; ?>" style="display: none;" /><br />
																<div class="form-group row">
																	<label class="col-2 col-form-label">Type d'information</label>
																	<div class="col-10">
																		<select class="form-control" name="type">
																			<option value="verif">Vérification</option>
																			<option value="probleme">Problème</option>
																			<option value="doublecompte">Double Compte</option>
																			<option value="suspicion">Suspicion</option>
																			<option value="autre">Autre</option>
																		</select>
																	</div>
																</div>
																
																 <div style="margin-top: 20px;"></div>
																
																 <div class="form-group text-left m-b-0">
																	<button class="btn btn-primary waves-effect waves-light" style="width: 100%;" type="submit">
																		Ajouter
																	</button>
																</div>
														</div>
													</div>
											</div>
										</div>
									</div>
								</div>
							</form>
					<?php
						
						}
						
					   }
					   
					  }
					
						require('includes/php/footer.php');
						
					?>