<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
	$dataByPage = 15;
	
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
					
						if (!hasPermission($groups, $rank, "section.moderation.multiAccountTool"))
						{
							noEnoughPermissions();
						}
						else
						{
							
							info("Le chargement de la page peut être long étant donné qu'une recherche dans l'ensemble de nos bases de données est effectuée.<br/>Il prend généralement une trentaine de secondes.<br/><br/>
							Un abus d'utilisation ou une utilisation non légale de ce système pourra valoir un dérank immédiat + un envoi de vos logs sous demande de réquisition pour une potentielle enquête cybercriminelle. BadBlock ne saurait être tenu responsable d'une utilisation illégale de cet outil et fournira toutes les logs en sa possession quant à l'utilisation de l'outil.");
					
							if (!isset($_GET['username']) && !isset($_GET['ip']))
							{
						
						?>
						
						<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Rechercher les doubles comptes à partir d'une IP</b></h4>

											<form action="multiaccounttool.php" method="get">
												<div class="row">
														<div class="p-20">
															<p class="mb-1"><b>Adresse IP</b></p>
															<input type="text" class="form-control" maxlength="25" name="ip" id="ip" />
															
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
						
						<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Rechercher les doubles comptes à partir d'un nom d'utilisateur</b></h4>

											<form action="multiaccounttool.php" method="get">
												<div class="row">
														<div class="p-20">
															<p class="mb-1"><b>Pseudo</b></p>
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
							else if (isset($_GET['username']))
							{
								
								$username = secure($db, $_GET['username']);
								
								$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT rank, mcname FROM users WHERE mcname = '".$username."'"));
								
								if (!canOverride($account, $user))
								{
									noEnoughPermissions();
								}
								else
								{
								
									$lastIp = mysqli_fetch_assoc(mysqli_query($db, "SELECT lastIp FROM friends WHERE pseudo = '".$username."'"));
								
									if ($lastIp == false)
									{
					
					?>
					<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Résultats de la recherche pour <?php echo $username; ?></b></h4>

											Aucune donnée trouvée pour le pseudonyme <?php echo $username; ?>.
										
										</div>
									</div>
						</div>
					<?php
					
									}
									else
									{
								
										$lastIp = $lastIp['lastIp'];
										
										$query  = mysqli_query($db, "SELECT pseudo FROM friends WHERE logs LIKE '%".secure($db, $lastIp)."%' ORDER BY id DESC;");
										
										$players = array();
										
										$permission = true;
										
										while ($data = mysqli_fetch_assoc($query))
										{
											if ($data == false)
											{
												continue;
											}
											$username = $data['pseudo'];
											$targetUser = mysqli_fetch_assoc(mysqli_query($db, "SELECT rank, mcname FROM users WHERE mcname = '".$username."'"));
											
											if (!canOverride($account, $targetUser))
											{
												$permission = false;
												break;
											}
											else
											{
												array_push($players, $data['pseudo']);
											}
										}
				
				?>
				
										<div class="row">
											<div class="col-sm-12">
												<div class="card-box">
													<h4 class="m-t-0 header-title"><b>Résultats de la recherche pour <?php echo $username; ?></b></h4>

				<?php
				
										if (!$permission)
										{
											
											noEnoughPermissions();
											
										}
										else
										{
											
											if (empty($players))
											{
												
										?>
													Aucune donnée trouvée pour le pseudonyme <?php echo $username; ?>.
												
				<?php

											}
											else
											{
												
												?>
												
												<b>Liste des pseudonymes reliés à son adresse IP:</b>
												<ul>
												
												<?php
												
												foreach ($players as $key)
												{
													echo '<li>'.$key.'</li>';
												}
												
												?>
												</ul>
												
			<?php
												
											}
											
										}
										
				?>
						
										
												</div>
											</div>
										</div>
										
				<?php
										
									}
								
								}
							
							}
							else if (isset($_GET['ip']))
							{
								
								$ip = secure($db, $_GET['ip']);
								
								$query  = mysqli_query($db, "SELECT pseudo FROM friends WHERE logs LIKE '%".secure($db, $ip)."%' ORDER BY id DESC;");
										
								$players = array();
										
								$permission = true;
										
								while ($data = mysqli_fetch_assoc($query))
								{
									
									if ($data == false)
									{
										continue;
									}
									
									$username = $data['pseudo'];
									$targetUser = mysqli_fetch_assoc(mysqli_query($db, "SELECT rank, mcname FROM users WHERE mcname = '".$username."'"));
											
									if (!canOverride($account, $targetUser))
									{
										$permission = false;
										break;
									}
									else
									{
										array_push($players, $data['pseudo']);
									}
									
								}
				
				?>
				
										<div class="row">
											<div class="col-sm-12">
												<div class="card-box">
													<h4 class="m-t-0 header-title"><b>Résultats de la recherche pour <?php echo $ip; ?></b></h4>

				<?php
				
										if (!$permission)
										{
											
											noEnoughPermissions();
											
										}
										else
										{
											
											if (empty($players))
											{
												
										?>
													Aucune donnée trouvée pour l'adresse IP <?php echo $ip; ?>.
												
				<?php

											}
											else
											{
												
												?>
												
												<b>Liste des pseudonymes reliés à cette adresse IP:</b>
												<ul>
												
												<?php
												
												foreach ($players as $key)
												{
													echo '<li>'.$key.'</li>';
												}
												
												?>
												</ul>
												
			<?php
												
											}
											
										}
										
				?>
						
										
												</div>
											</div>
										</div>
										
				<?php
										
							}
							
						}
						
						require('includes/php/footer.php');
				
				?>