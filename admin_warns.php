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
					
						if (!hasPermission($groups, $rank, "section.administration.warns"))
						{
							
							noEnoughPermissions();
						
						}
						else
						{
					
					?>
						
							<div class="row">
								<div class="col-sm-12">
									<h4 class="page-title">Avertisssements</h4>
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
											<h4 class="m-t-0 header-title"><b>Ajouter un avertissement</b></h4>

										<form action="admin_warns-work.php" method="post">
											<div class="row">
													<div class="col-md-12">
														<div class="p-20">
																<div class="form-group row">
																	<label class="col-2 col-form-label">Utilisateur</label>
																	<div class="col-10">
																		<select class="form-control" name="username">
																		<?php
																		
																			$query = mysqli_query($db, "SELECT rank, mcname FROM users WHERE rank < ".secure($db, $account['rank'])." AND disabled = 0 ORDER BY rank DESC, mcname ASC;");
																	
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
																	<label>Détails</label>
																	<div>
																		<input class="form-control" type="text" name="message" value=""/>
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
							
							?>
							 <div class="row">
								<div class="col-sm-12">
									<div class="card-box">
										<h4 class="m-t-0 header-title"><b>Avertissements</b> <div class="columns columns-right btn-group pull-right"><form method="post"><button class="btn btn-default" type="submit" name="paginationSwitch" aria-label="pagination Switch" title="Ajouter une absence">+</button></form></div></h4>
										<p class="text-muted font-13">
											Vous pouvez ajouter un avertissement pour l'utilisateur. Au bout de 3 avertissements => viré.
										</p>

										<table data-toggle="table"
											   data-show-columns="false"
											   data-page-list="[5, 10, 20]"
											   data-page-size="5"
											   data-pagination="true" data-show-pagination-switch="true" class="table-bordered ">
											<thead>
											<tr>
												<th data-field="username" data-switchable="false">Nom d'utilisateur</th>
												<th data-field="addedBy">Ajouté par</th>
												<th data-field="date">Date</th>
												<th data-field="message">Message</th>
											</tr>
											</thead>

											<tbody>
											<?php
												$query = mysqli_query($db, "SELECT * FROM warns ORDER BY id DESC;");
												while ($data = mysqli_fetch_assoc($query))
												{
													?>
													<tr>
														<td><?php echo secure($db, $data['username']); ?></td>
														<td><?php echo secure($db, $data['addedBy']); ?></td>
														<td><?php echo secure($db, $data['date']); ?></td>
														<td><?php echo secure($db, $data['message']); ?></td>
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