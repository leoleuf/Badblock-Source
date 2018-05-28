<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
	$dataByPage = 13;
	
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
					
						if (!hasPermission($groups, $rank, "section.moderation.nicks"))
						{
							noEnoughPermissions();
						}
						else
						{
					
							if (!isset($_GET['username']))
							{
						
						?>
						
						<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Rechercher les derniers surnoms</b></h4>

											<form action="nicks.php" method="get">
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
								
								$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT rank, mcname FROM users WHERE mcname = '".$username."'"));
								
								if (!canOverride($account, $user))
								{
									noEnoughPermissions();
								}
								else
								{
																
									$count = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM nickLogs WHERE playerName = '".$username."';"));
									$count = $count['count'];
							
							?>
							
								<div class="row">
									<div class="col-sm-12">
										<h4 class="page-title">Surnoms par <?php echo $username; ?> (<?php echo formatInt($count); ?>)</h4>
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
														  <th>Surnom</th>
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
													
														$request = mysqli_query($db, "SELECT type, playerName, date, nick FROM nickLogs WHERE playerName = '".$username."' ORDER BY id DESC LIMIT ".$firstEntry.", ".$dataByPage.";");
															
														while ($data = mysqli_fetch_assoc($request))
														{
															
															$type = "";
															$v = "Inconnu";
															
															if ($data['type'] == "EDIT") 
															{
																$type = "success";
																$v = "Changement";
															}
															else if ($data['type'] == "REMOVE") 
															{
																$type = "danger";
																$v = "Suppression";
															}
															
													?>
														<tr>
															<td><?php echo $data['playerName']; ?></td>
															<td><?php echo secure($db, $data['date']); ?></td>
															<td><span class="label label-<?php echo $type; ?>"><?php echo $v; ?></span></td>
															<td><?php echo $data['nick']; ?></td>
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
							<?php
								
									}
									
								}
								
							}
							
								require('includes/php/footer.php');
								
							?>