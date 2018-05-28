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
	
?>

			<link href="plugins/timepicker/bootstrap-timepicker.min.css" rel="stylesheet">
			<link href="plugins/bootstrap-colorpicker/css/bootstrap-colorpicker.min.css" rel="stylesheet">
			<link href="plugins/bootstrap-datepicker/css/bootstrap-datepicker.min.css" rel="stylesheet">
			<link href="plugins/clockpicker/css/bootstrap-clockpicker.min.css" rel="stylesheet">
			<link href="plugins/bootstrap-daterangepicker/daterangepicker.css" rel="stylesheet">

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
					
						if (!hasPermission($groups, $rank, "section.moderation.absents"))
						{
							
							noEnoughPermissions();
						
						}
						else
						{
					
					?>
						
							<div class="row">
								<div class="col-sm-12">
									<h4 class="page-title">Absences de la modération</h4>
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
											<h4 class="m-t-0 header-title"><b>Ajouter une absence</b></h4>

										<form action="mod-absents-work.php" method="post">
											<div class="row">
													<div class="col-md-12">
														<div class="p-20">
																<div class="form-group row">
																	<label class="col-2 col-form-label">Utilisateur</label>
																	<div class="col-10">
																		<select class="form-control" name="username">
																		<?php
																		
																			$query = mysqli_query($db, "SELECT rank, mcname FROM users WHERE rank = 55 || rank = 50 || rank = 40 || rank = 30 ORDER BY rank DESC, mcname ASC;");
																	
																			while ($data = mysqli_fetch_assoc($query))
																			{
																				$mcname = secure($db, $data['mcname']);
																				$rank = secure($db, $data['rank']);
																		
																		?>
																			<option value="<?php echo $mcname; ?>"><?php echo $mcname; ?> - <?php echo $groups[$rank]['name']; ?></option>
																		<?php
																		
																			}
																		
																		?>
																		</select>
																	</div>
																</div>

																<div class="form-group m-b-20">
																	<label>Plage datée de l'absence</label>
																	<div>
																		<input class="form-control input-daterange-datepicker" type="text" name="date" value="<?php echo date("m/d/Y") ?> - <?php echo date("m/d/Y", strtotime("+7 days")); ?>"/>
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
									
									if (isset($_GET['remove']))
									{
										$remove = intval($_GET['remove']);
										$absents = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM absents WHERE id = '".$remove."'"));
										if ($absents['count'] > 0)
										{
											mysqli_query($db, "DELETE FROM absents WHERE id = '".$remove."'");
											success('Cette absence est supprimée.');
										}
										else
										{
											error('Cette absence n\'existe pas.');
										}
									}
							
							?>
							 <div class="row">
								<div class="col-sm-12">
									<div class="card-box">
										<h4 class="m-t-0 header-title"><b>Absences de la modération</b> <div class="columns columns-right btn-group pull-right"><form method="post"><button class="btn btn-default" type="submit" name="paginationSwitch" aria-label="pagination Switch" title="Ajouter une absence">+</button></form></div></h4>
										<p class="text-muted font-13">
											Les absences de la modération seront notées ici. Cela permettra d'être mieux organisé.
										</p>

										<table data-toggle="table"
											   data-show-columns="false"
											   data-page-list="[5, 10, 20]"
											   data-page-size="5"
											   data-pagination="true" data-show-pagination-switch="true" class="table-bordered ">
											<thead>
											<tr>
												<th data-field="name" data-switchable="false">Nom d'utilisateur</th>
												<th data-field="startTime">Date de début</th>
												<th data-field="endTime">Date de fin</th>
												<th data-field="addedBy">Ajouté par</th>
												<th data-field="actions" class="text-center">Actions</th>
											</tr>
											</thead>

											<tbody>
											<?php
												$query = mysqli_query($db, "SELECT * FROM absents WHERE section = 'moderation' ORDER BY id DESC;");
												while ($data = mysqli_fetch_assoc($query))
												{
													?>
													<tr>
														<td><?php echo secure($db, $data['username']); ?></td>
														<td><?php echo date("d/m/Y", $data['startTime']); ?></td>
														<td><?php echo date("d/m/Y", $data['endTime']); ?></td>
														<td><?php echo secure($db, $data['addedBy']); ?></td>
														<td><a href="?remove=<?php echo secure($db, $data['id']); ?>"><img src="images/delete.png" width="16" height="16" /></a></span></td>
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