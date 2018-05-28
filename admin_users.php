<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
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
					
						if (!hasPermission($groups, $rank, "section.administration") OR !hasPermission($groups, $rank, "section.administration.users"))
						{
							noEnoughPermissions();
						}
						else
						{
					
					?>
						
							<?php
							
									if (isset($error))
									{
										?>
										<div class="alert alert-danger">
										  <strong>Erreur!</strong> <?php echo $error; ?>
										</div>
										<?php
									}
									
									if (isset($_SESSION['success']))
									{
										success($_SESSION['success']);
										unset($_SESSION['success']);
									}
									
									if (isset($_POST['createUser']))
									{
										
										$done = false;
										$password = null;
										
										if (isset($_POST['username']))
										{
											if (isset($_POST['email']))
											{
												if (filter_var($_POST['email'], FILTER_VALIDATE_EMAIL))
												{
													if (isset($_POST['rank']))
													{
														$username = secure($db, $_POST['username']);
														if (strlen($username) > 2 && strlen($username) < 16)
														{
															$email = secure($db, $_POST['email']);
															$rank = secure($db, $_POST['rank']);
															$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM users WHERE mcname = '".$username."'"));
															if ($user == false || $user['count'] == 0)
															{
																$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM users WHERE mcname = '".$username."'"));
																if ($user == false || $user['count'] == 0)
																{
																	if (getPermission($groups, $account['rank'], "power.level") >= $rank OR hasPermission($groups, $account['rank'], "bypass.assignlevel") OR ($rank == $account['rank'] && hasPermission($groups, $account['rank'], "bypass.level")))
																	{
																		if (isset($groups[$rank]))
																		{
																			$password = generateRandomString(16);
																			$hashedPassword = encode($password, $email);
																			mysqli_query($db, "INSERT INTO users(email, mcname, password, rank) VALUES('".$email."', '".$username."', '".$hashedPassword."', '".$rank."')");
																			$done = true;
																		}
																		else
																		{
																			error('Privilège inconnu.');
																		}
																	}
																	else
																	{
																		error('Vous ne pouvez pas mettre ce privilège.');
																	}
																}
																else
																{
																	error('Cette adresse email est déjà utilisée sur le Manager.');
																}
															}
															else
															{
																error('Ce pseudonyme existe déjà sur le Manager.');
															}
														}
														else
														{
															error('Votre pseudo Minecraft doit faire entre 2 et 16 caractères au minimum.');
														}
													}
													else
													{
														error('Veuillez préciser le grade du joueur.');
													}
												}
												else
												{
													error('Cette adresse e-mail est invalide.');
												}
											}
											else
											{
												error('Veuillez préciser votre adresse email.');
											}
											
										}
										
										if ($done)
										{
											success('Ce compte a été créé avec succès. Mot de passe généré: <b>'.$password.'</b><br /><a href="admin_users.php">Retour</a>');
										}
										
									?>
									<div class="row">
													<div class="col-sm-12">
														<div class="card-box">
														<form method="post" action="admin_users.php">
															<h4 class="m-t-0 header-title"><b>Utilisateur</b></h4>
															<div style="margin-top: 20px;"></div>
															<p class="text-muted font-13">
																<div class="form-group row">
																		<label class="col-2 col-form-label">Adresse email : </label>
																		<div class="col-10">
																			<input type="text" class="form-control" name="email" id="email" value="" />
																		</div>
																	</div>
																<div class="form-group row">
																		<label class="col-2 col-form-label">Utilisateur : </label>
																		<div class="col-10">
																			<input type="text" class="form-control" maxlength="25" name="username" id="username" value="" />
																		</div>
																	</div>

																	<div class="form-group row">
																		<label class="col-2 col-form-label">Grade : </label>
																		<div class="col-10">
																			<select class="form-control" name="rank">
																			<?php
																			
																				foreach ($groups as $key => $value)
																				{
																					
																					$selected = '';
																					
																					if ($account['rank'] < $key)
																					{
																						$selected = ' disabled';
																					}else if ($user['rank'] == $key)
																					{
																						$selected = ' selected';
																					}
																					
																					$mcname = secure($db, $user['mcname']);
																					$rank = secure($db, $user['rank']);
																			
																			?>
																				<option value="<?php echo $key; ?>"<?php echo $selected; ?>><?php echo $groups[$key]['name']; ?></option>
																			<?php
																			
																				}
																			
																			?>
																			</select>
																		</div>
																	</div>

																	 <div style="margin-top: 20px;"></div>
																	
																	 <div class="form-group text-left m-b-0">
																		<input text="submit" name="createUser" style="display: none;" />
																		<button class="btn btn-primary waves-effect waves-light" style="width: 100%;" type="submit">
																			Ajouter
																		</button>
																	</div>
															</p>
														</form>
														</div>
													</div>
												</div>
									<?php
									
									}
									else if (isset($_GET['id']))
									{
										
										$id = intval($_GET['id']);
										$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT mcname, email, rank FROM users WHERE id = '".$id."'"));
										
										if ($user == false)
										{
											
											?>
											<div class="row">
												<div class="col-sm-12">
													<div class="card-box">
														<h4 class="m-t-0 header-title"><b>Utilisateur inconnu</b></h4>
														<p class="text-muted font-13">
															Cet utilisateur est inconnu.
														</p>
													</div>
												</div>
											</div>
											<?php
											
										}
										else
										{
											
											if (!canOverride($account, $user))
											{
												
												?>
												<div class="row">
												<div class="col-sm-12">
													<div class="card-box">
														<h4 class="m-t-0 header-title"><b>Utilisateur non modifiable</b></h4>
														<p class="text-muted font-13">
															Cet utilisateur n'est pas modifiable. Vous n'avez pas la permission de voir les détails de ce compte utilisateur.
														</p>
													</div>
												</div>
											</div>
												<?php
												
											}
											else
											{
											
												if (isset($_GET['type']))
												{
													
													$type = secure($db, $_GET['type']);
													
													if ($type === 'details')
													{
														
														$edited = false;
														
														if (isset($_POST['username']))
														{
															$newUsername = secure($db, $_POST['username']);
															if (strcmp($user['mcname'], $newUsername) != 0)
															{
																if (strlen($newUsername) < 2 || strlen($newUsername) > 16)
																{
																	error('Le nouveau nom d\'utilisateur doit faire entre 2 et 16 caractères.');
																}
																else
																{
																	if (!hasPermission($groups, $account['rank'], "section.administration.users.edit.rank"))
																	{
																		error('Vous n\'avez pas le privilège de pouvoir modifier le pseudo Minecraft.');
																	}
																	else
																	{
																		$us = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM users WHERE mcname = '".$newUsername."'"));
																		if ($us == false || $us['count'] < 1)
																		{
																			mysqli_query($db, "UPDATE users SET mcname = '".secure($db, $newUsername)."' WHERE id = '".$id."'");
																			$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT mcname, email, rank FROM users WHERE id = '".$id."'"));
																			$edited = true;
																		}
																		else
																		{
																			error('Ce nom d\'utilisateur est déjà utilisé.');
																		}
																	}
																}
															}
														}
														
														if (isset($_POST['rank']))
														{
															$rank = secure($db, $_POST['rank']);
															if ($rank != $account['rank'])
															{
																if (!hasPermission($groups, $account['rank'], "section.administration.users.edit.rank"))
																{
																	error('Vous n\'avez pas le privilège de pouvoir modifier le grade de ce membre.');
																}
																else
																{
																	if (getPermission($groups, $account['rank'], "power.level") >= $rank OR hasPermission($groups, $account['rank'], "bypass.assignlevel") OR ($rank == $account['rank'] && hasPermission($groups, $account['rank'], "bypass.level")))
																	{
																		if (isset($groups[$rank]))
																		{
																			mysqli_query($db, "UPDATE users SET rank = '".secure($db, $rank)."' WHERE id = '".$id."'");
																			$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT mcname, email, rank FROM users WHERE id = '".$id."'"));
																			$edited = true;
																		}
																		else
																		{
																			error('Privilège inconnu.');
																		}
																	}
																	else
																	{
																		error('Vous ne pouvez pas mettre ce privilège.');
																	}
																}
															}
														}
														
														if ($edited)
														{
															success('Vous avez modifié les détails de ce compte avec succès.');
														}
														
											
											?>
												<div class="row">
													<div class="col-sm-12">
														<div class="card-box">
														<form method="post" action="admin_users.php?id=<?php echo $id; ?>&type=<?php echo $type; ?>">
															<h4 class="m-t-0 header-title"><b>Utilisateur : <?php echo secure($db, $user['mcname']); ?></b></h4>
															<div style="margin-top: 20px;"></div>
															<p class="text-muted font-13">
																<div class="form-group row">
																		<label class="col-2 col-form-label">Adresse email : </label>
																		<div class="col-10">
																			<input type="text" class="form-control" name="email" id="username" value="<?php echo secure($db, $user['email']); ?>" disabled />
																		</div>
																	</div>
																<div class="form-group row">
																		<label class="col-2 col-form-label">Utilisateur : </label>
																		<div class="col-10">
																			<input type="text" class="form-control" maxlength="25" name="username" id="username" value="<?php echo secure($db, $user['mcname']); ?>" />
																		</div>
																	</div>

																	<div class="form-group row">
																		<label class="col-2 col-form-label">Grade : </label>
																		<div class="col-10">
																			<select class="form-control" name="rank">
																			<?php
																			
																				foreach ($groups as $key => $value)
																				{
																					
																					$selected = '';
																					
																					if ($account['rank'] < $key)
																					{
																						$selected = ' disabled';
																					}else if ($user['rank'] == $key)
																					{
																						$selected = ' selected';
																					}
																					
																					$mcname = secure($db, $user['mcname']);
																					$rank = secure($db, $user['rank']);
																			
																			?>
																				<option value="<?php echo $key; ?>"<?php echo $selected; ?>><?php echo $groups[$key]['name']; ?></option>
																			<?php
																			
																				}
																			
																			?>
																			</select>
																		</div>
																	</div>

																	 <div style="margin-top: 20px;"></div>
																	
																	 <div class="form-group text-left m-b-0">
																		<button class="btn btn-primary waves-effect waves-light" style="width: 100%;" type="submit">
																			Modifier
																		</button>
																	</div>
															</p>
														</form>
														</div>
													</div>
												</div>
										<?php
												
													}
													else if ($type === 'status')
													{
														
														$data = mysqli_fetch_assoc(mysqli_query($db, "SELECT disabled FROM users WHERE id = '".$id."'"));
														
														$now = $data['disabled'] == 1 ? 0 : 1;
														$shown = $now ? "désactivé" : "réactivé";
														mysqli_query($db, "UPDATE users SET disabled = '".$now."' WHERE id = '".$id."'");
														success("Le compte n°".$id." a bien été ".$shown.". <a href='admin_users.php'>Retour</a>");
														
													}
													else if ($type === 'removedoubleauth')
													{
														
														mysqli_query($db, "UPDATE users SET secret = '', tempToken = '' WHERE id = '".$id."'");
														success("La double authentification du compte n°".$id." a bien été remise à zéro. <a href='admin_users.php'>Retour</a>");
														
													}
										
												}
												else
												{
													
										?>
											<div class="row">
												<div class="col-sm-12">
													<div class="card-box">
														<h4 class="m-t-0 header-title"><b>Utilisateur : <?php echo secure($db, $user['mcname']); ?></b></h4>
														<div style="margin-top: 20px;"></div>
														<p class="text-muted font-13">
															Type d'action inconnu.
														</p>
													</div>
												</div>
											</div>
										<?php
													
												}
												
											}
										
										}
										
									}
									else
									{
									
										$enabledCount = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(*) AS count FROM users WHERE disabled = '0'"));
									
							?>
							
								 <div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Utilisateurs activés (<?php echo $enabledCount['count']; ?>)</b> <div class="columns columns-right btn-group pull-right"><form method="post"><button class="btn btn-default" type="submit" name="createUser" aria-label="pagination Switch" title="Ajouter un utilisateur">+</button></form></div></h4>
											<p class="text-muted font-13">
												Vous retrouverez tous les utilisateurs actifs ici. Vous pouvez par conséquent en désactiver certains, remettre à zéro la double authentification, modifier des informations. 
											</p>

											<table data-toggle="table"
												   data-show-columns="false"
												   data-page-list="[5, 10, 20, 50, 100, 500, 1000]"
												   data-page-size="100"
												   data-pagination="true" data-show-pagination-switch="true" class="table-bordered ">
												<thead>
												<tr>
													<th data-field="id" data-switchable="false">ID</th>
													<th data-field="email">Email</th>
													<th data-field="mcname">Pseudo Minecraft</th>
													<th data-field="status">Status</th>
													<th data-field="rank">Grade</th>
													<th data-field="doubleauth">Double authentification</th>
												</tr>
												</thead>

												<tbody>
												<?php
												
													$query = mysqli_query($db, "SELECT disabled, id, email, mcname, rank FROM users WHERE disabled = '0' ORDER BY rank DESC, mcname ASC;");
													while ($data = mysqli_fetch_assoc($query))
													{
														
														$activated = $data['disabled'] == 0 ? "<font color='red'>Désactiver le compte</font>" : "<font color='green'>Réactiver le compte</font>";
														
												?>
														<tr>
															<td><b><?php echo secure($db, $data['id']); ?></b></td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=details">'.secure($db, $data['email']).'</a>';
																}
																else
																{
																	echo secure($db, $data['email']);
																}
															?>
															</td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=details">'.secure($db, $data['mcname']).'</a>';
																}
																else
																{
																	echo secure($db, $data['mcname']);
																}
															?>
															</td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit.status"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=status">'.$activated.'</a>';
																}
																else
																{
																	echo $activated;
																}
															?>
															</td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit.rank"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=details">'.secure($db, $groups[$data['rank']]['name']).'</a>';
																}
																else
																{
																	echo secure($db, $groups[$data['rank']]['name']);
																}
															?>
															</td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit.doubleauth"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=removedoubleauth">Remettre à zéro la double authentification</a>';
																}
																else
																{
																	echo '<b>Permission refusée</b>';
																}
															?>
															</td>
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
									
									$disabledCount = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(*) AS count FROM users WHERE disabled = '1';"));
										
								?>
								
								 <div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Utilisateurs désactivés (<?php echo $disabledCount['count']; ?>)</b> <div class="columns columns-right btn-group pull-right"><form method="post"><button class="btn btn-default" type="submit" name="createUser" aria-label="pagination Switch" title="Ajouter un utilisateur">+</button></form></div></h4>
											<p class="text-muted font-13">
												Vous retrouverez tous les utilisateurs désactivés ici. Vous pouvez par conséquent en réactiver certains.
											</p>

											<table data-toggle="table"
												   data-show-columns="false"
												   data-page-list="[5, 10, 20, 50, 100, 500, 1000]"
												   data-page-size="100"
												   data-pagination="true" data-show-pagination-switch="true" class="table-bordered ">
												<thead>
												<tr>
													<th data-field="id" data-switchable="false">ID</th>
													<th data-field="email">Email</th>
													<th data-field="mcname">Pseudo Minecraft</th>
													<th data-field="status">Status</th>
													<th data-field="rank">Grade</th>
													<th data-field="doubleauth">Double authentification</th>
												</tr>
												</thead>

												<tbody>
												<?php
												
													$query = mysqli_query($db, "SELECT disabled, id, email, mcname, rank FROM users WHERE disabled = '1' ORDER BY rank DESC, mcname ASC;");
													while ($data = mysqli_fetch_assoc($query))
													{
														
														$activated = $data['disabled'] == 0 ? "<font color='red'>Désactiver le compte</font>" : "<font color='green'>Réactiver le compte</font>";
														
												?>
														<tr>
															<td><b><?php echo secure($db, $data['id']); ?></b></td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=details">'.secure($db, $data['email']).'</a>';
																}
																else
																{
																	echo secure($db, $data['email']);
																}
															?>
															</td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=details">'.secure($db, $data['mcname']).'</a>';
																}
																else
																{
																	echo secure($db, $data['mcname']);
																}
															?>
															</td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit.status"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=status">'.$activated.'</a>';
																}
																else
																{
																	echo $activated;
																}
															?>
															</td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit.rank"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=details">'.secure($db, $groups[$data['rank']]['name']).'</a>';
																}
																else
																{
																	echo secure($db, $groups[$data['rank']]['name']);
																}
															?>
															</td>
															<td>
															<?php
																if (hasPermission($groups, $account['rank'], "section.administration.users.edit.doubleauth"))
																{
																	echo '<a href="admin_users.php?id='.secure($db, $data['id']).'&type=removedoubleauth">Remettre à zéro la double authentification</a>';
																}
																else
																{
																	echo '<b>Permission refusée</b>';
																}
															?>
															</td>
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