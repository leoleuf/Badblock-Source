<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
	require_once __DIR__ . '/vendor/autoload.php';
	use PhpAmqpLib\Connection\AMQPStreamConnection;
	use PhpAmqpLib\Message\AMQPMessage;
	
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
								
					if (!isset($_GET['id']) OR !is_numeric($_GET['id']))
					{
						?>
						<div class="row">
						  <div class="col-sm-12">
							<div class="card-box">
								<h4 class="m-t-0 header-title"><b>ID invalide</b></h4>
									
									Un ID valide est requis.
							</div>
						  </div>
						</div>
						<?php
					}
					else
					{
						
						if (!hasPermission($groups, $rank, "section.moderation.case"))
						{
							noEnoughPermissions();
						}
						else
						{
					
							$id = intval($_GET['id']);
							
							$request2 = mysqli_query($db, "SELECT * FROM sanctions WHERE id = '".$id."'");
							$data2 = mysqli_fetch_assoc($request2);
							if ($data2 == false)
							{
								
						?>
							<div class="row">
							  <div class="col-sm-12">
								<div class="card-box">
									<h4 class="m-t-0 header-title"><b>ID inconnu</b></h4>
										La sanction précisée n'est pas valide. Veuillez réessayer.
								</div>
							  </div>
							</div>
						
						<?php
									
							}
							else
							{
								
								$type = $punishColors[$data2['type']];
								$v = $punishTypes[$data2['type']];
								
								$time =  ($data2['expire'] / 1000) - ($data2['timestamp'] / 1000);
								$days = floor($time / (60 * 60 * 24));
								$time -= $days * (60 * 60 * 24);
								$hours = floor($time / (60 * 60));
								$time -= $hours * (60 * 60);
								$minutes = floor($time / 60);
								$time -= $minutes * 60;
								$seconds = floor($time);
								$time -= $seconds;
								if ($data2['expire'] != -1) $time = ($days > 0 ? $days."j" : "").($hours > 0 ? $hours."h" : "").($minutes > 0 ? $minutes."m" : "").($seconds > 0 ? $seconds."s" : "");
								else if ($data2['type'] == "ban" OR $data2['type'] == "banip")
								$time = "Définitif";
								else $time = "—";
								
						?>
								<div class="row">
							  <div class="col-sm-12">
								<div class="card-box">
									<h4 class="m-t-0 header-title"><b>Sanction n°<?php echo $data2['id']; ?></b></h4>
									<h3>Informations sur la sanction</h3>
									<div style="margin-left: 2%">
										<b>Pseudo:</b> <?php echo $data2['pseudo']; ?><br />
										<b>Date:</b> <?php echo $data2['date']; ?><br />
										<b>Type:</b> <span class="label label-<?php echo $type; ?>"><?php echo $v; ?></span><br />
										<b>Par:</b> <?php echo $data2['banner']; ?><br />
										<b>Temps:</b> <?php echo $time; ?><br />
										<b>Raison:</b> <?php echo translateReason($data2['reason']); ?>
									</div><hr />
								<h2>Preuves</h2>
									<div style="margin-left: 2%">
								</div>
							  </div>
							</div>
							</div>
							<?php
							
								if (isset($_POST['proof']))
								{
									$proof = secure($db, $_POST['proof']);
									if (strlen($proof) > 500)
									{
										error('Le champ des preuves est limité à 500 caractères.');
									}
									else
									{
										if (strpos(strtolower($proof), 'proofguardianer.php') !== false)
										{
											error('Vous ne pouvez pas ajouter une preuve Guardianer. Elle s\'ajoute toute seule !');
										}
										else if (strpos(strtolower($data2['proof']), 'proofguardianer.php') !== false)
										{
											error('Vous ne pouvez pas modifier quelque chose de déjà prouvé automatiquement !');
										}
										else
										{
											$editQuery = mysqli_query($db, "SELECT player FROM proofEdits WHERE sanction = '".$id."'");
											$permission = true;
											
											while ($editData = mysqli_fetch_assoc($editQuery))
											{
												$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT mcname, rank FROM users WHERE id = '".secure($db, $editData['player'])."'"));
												if (!canOverride($account, $user))
												{
													$permission = false;
												}
											}
											
											if (!$permission)
											{
												error('Quelqu\'un de plus haut gradé que vous a modifié cette preuve. Elle ne peut donc pas être modifiable.');
											}
											else
											{
												mysqli_query($db, "UPDATE sanctions SET proof = '".$proof."' WHERE id = '".$id."'");
												mysqli_query($db, "INSERT INTO proofEdits(sanction, player, `from`, `to`) VALUES('".$id."', '".secure($db, $account['id'])."', '".secure($db, $data2['proof'])."', '".$proof."')");
												success('Le champ des preuves a été modifié.');
												$request2 = mysqli_query($db, "SELECT * FROM sanctions WHERE id = '".$id."'");
												$data2 = mysqli_fetch_assoc($request2);
											}
										}
									}
								}
								
								$disabledButton = strpos(strtolower($data2['proof']), 'proofguardianer.php') !== false ? ' disabled' : '';
								if ($disabledButton != '')
								{
									$disabledText = 'Cette preuve n\'est pas modifiable. Elle a été ajoutée automatiquement.';
								}
								else
								{
									$disabledText = '';
									foreach(preg_split("/((\r?\n)|(\r\n?))/", $data2['proof']) as $line)
									{
										if (!startsWith($line, "proofs/")) $disabledText .= $line.PHP_EOL;
									}
								}
							
							?>
							<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Ajouter/Modifier une preuve</b></h4>

											<table id="tech-companies-1" class="table  table-striped">
																<thead>
																	<tr>
																		<th>Preuve</th>
																		<th>Actions</th>
																	</tr>
																	<tr>
																		<?php

																			$splitter = explode(' 
', $data2['proof']);
																			var_dump($splitter);
																			foreach($splitter as $key)
																			{
																				echo '<td>'.$key.'</td>
																				';
																			}
																		?>
																	</tr>
																</thead>
																<tbody>
																</tbody>
											</table>
											
											<form enctype="multipart/form-data" action="?id=<?php echo $data2['id']; ?>" method="post">
												<div class="row">
													<div class="col-md-12">
														<div class="p-20">
															
																<div class="form-group row">
																	<label class="col-2 col-form-label">Liens</label>
																	<div class="col-10">
																		<textarea class="form-control" rows="5" name="proof">
																		<?php
																			echo stripcslashes($data2['proof']);
																		?>
																		</textarea>
																	</div>
																</div>
																 <div class="form-group text-left m-b-0">
																	<button class="btn btn-primary waves-effect waves-light" style="width: 100%;" name="apply"  type="submit">
																		Modifier
																	</button>
																</div>
															</div>
													</div>
												</div>
											</form>
											</div>
										</div>
									</div>
						
				<?php
								
							}
											
						}
					
					}
				
									
					require('includes/php/footer.php');
									
				?>