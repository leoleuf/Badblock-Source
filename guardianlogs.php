<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	require_once('includes/php/database_guardian.php');
	
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
					
					if (!hasPermission($groups, $rank, "section.moderation.guardian"))
					{
						noEnoughPermissions();
					}
					else
					{
					
						// TODO: beurk
						if (!isset($_GET['username']) && !isset($_GET['id']))
						{
					
					?>
					
					<div class="row">
								<div class="col-sm-12">
									<div class="card-box">
										<h4 class="m-t-0 header-title"><b>Rechercher les logs Guardian d'un utilisateur</b></h4>

										<form action="guardianlogs.php" method="get">
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
						else if (isset($_GET['id']))
						{
							$id = intval($_GET['id']);
							$data = mysqli_fetch_assoc(mysqli_query($guardianDb, "SELECT * FROM logs WHERE id = '".$id."';"));
							if ($data == false)
							{
								error('Cette log Guardian n\'existe pas.');
							}
							else
							{
								$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT rank, mcname FROM users WHERE mcname = '".secure($db, $data['username'])."'"));
								if (canOverride($account, $user))
								{
									?>
									<div class="row">
										<div class="col-sm-12">
											<h4 class="page-title">Log n°<?php echo $id; ?> - <?php echo secure($db, $data['username']); ?> : <?php echo secure($db, translateReason($data['cheat'])); ?></h4>
											<p class="text-muted page-title-alt"></p>
										</div>
									</div>
									
									<div class="row">
											<div class="col-sm-12">
												<div class="card-box">
												<?php
													echo translateReason(nl2br($data['logs']));
												?>
												</div>
										</div>
									</div>
									<?php
								}
								else
								{
									error('Vous n\'avez pas la permission de voir les logs Guardian de cet utilisateur.');
								}
							}
						}
						else
						{
							
							$username = secure($guardianDb, $_GET['username']);
							$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT rank, mcname FROM users WHERE mcname = '".secure($db, $username)."'"));
							if (!canOverride($account, $user))
							{
								error('Vous n\'avez pas la permission de voir les logs Guardian de cet utilisateur.');
							}
							else
							{
							
							$count = mysqli_fetch_assoc(mysqli_query($guardianDb, "SELECT COUNT(id) AS count FROM logs WHERE username = '".$username."';"));
							$count = $count['count'];
					
					?>
					
                        <div class="row">
                            <div class="col-sm-12">
                                <h4 class="page-title">Logs Guardian de <?php echo $username; ?> (<?php echo formatInt($count); ?>)</h4>
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
													<th>Cheat</th>
													<th>Sanction</th>
													<th>Version</th>
													<th>Logs</th>
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
											
												$request = mysqli_query($guardianDb, "SELECT type, id, username, date, cheat, version FROM logs WHERE username = '".$username."' ORDER BY id DESC LIMIT ".$firstEntry.", ".$dataByPage.";");
													
												while ($data = mysqli_fetch_assoc($request))
												{
													
													$punishType = translatePunishType($data['type']);
													$punishColor = translatePunishColor($data['type']);
											?>
												<tr>
													<td><a href="/guardianlogs.php?username=<?php echo secure($guardianDb, $data['username']); ?>"><?php echo secure($db, $data['username']); ?></a></td>
													<td><?php echo secure($guardianDb, $data['date']); ?></td>
													<td><?php echo secure($guardianDb, translateReason($data['cheat'])); ?></td>
													<td><span class="label label-<?php echo $punishColor; ?>"><?php echo $punishType; ?></span></td>
													<td><?php echo secure($guardianDb, $data['version']); ?></td>
													<td><a href="/guardianlogs.php?id=<?php echo secure($guardianDb, $data['id']); ?>">Détails</a></td>
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