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
								
					if (!hasPermission($groups, $rank, "section.moderation.guardianer"))
					{
						noEnoughPermissions();
					}
					else
					{
				
						if (isset($_POST['confirm']))
						{
							$request = mysqli_query($db, "SELECT * FROM reportMsg WHERE `done` = 'false' AND playerTo = '".secure($db, $account['mcname'])."'");
							while ($data = mysqli_fetch_assoc($request))
							{
								if (isset($_POST['mute_'.$data['id']]))
								{
									
									$connection = new AMQPStreamConnection('127.0.0.1', 5672, 'root', '7SVog3hqbZZGvVb5JswMKVCrd3BuIQNGyzUFt1WuRL0cw275luIWfadi4EDClg8KGTgZL2jsEau13sXeMv0Rv40pztSKARl7ArWck6uO6PF08s4KNVs9PiofC7Q3O10e', 'rabbit');
									
									$channel = $connection->channel();
									$date = date("d/m/Y H:i:s");
									
									$user = mysqli_fetch_assoc(mysqli_query($db, "SELECT mcname, rank FROM users WHERE mcname = '".secure($db, $data['player'])."'"));
									
									if (canOverride($account, $user))
									{
									
										mysqli_query($db, "INSERT INTO proofGuardianer(message, pseudo, date, banner) VALUES('".secure($db, $data['message'])."', '".secure($db, $data['player'])."', '".secure($db, $date)."', '".secure($db, $account['mcname'])."')");
										
										$lastid = mysqli_insert_id($db);
										
										$channel->queue_declare('sanction', false, false, false, false);
											$sanction = (object)
											[
												'pseudo' => secure($db, $data['player']),
												'type' => 'AUTO',
												'expire' => 0,
												'timestamp' => time() * 1000,
												'reason' => "flood_couleur_spam_insulte_provocation_discrimination_citation_serveur_3",
												'banner' => secure($db, $account['mcname']),
												'fromIp' => $_SERVER['REMOTE_ADDR'],
												'proof' => 'https://manager.badblock.fr/guardianerProof.php?id='.$lastid,
												'auto' => true
											];
											
											$message = (object) 
											[
												'expire' => (time() + 604800) * 1000,
												'message' => json_encode($sanction)
											];
											
											$msg = new AMQPMessage(json_encode($message));
											$channel->basic_publish($msg, '', 'sanction');
										
											$channel->close();
											$connection->close();

										mysqli_query($db, "UPDATE reportMsg SET `done` = 'true', accepted = 'true' WHERE id = '".secure($db, $data['id'])."'");
									}
								
								}
								else
								{
									mysqli_query($db, "UPDATE reportMsg SET `done` = 'true', accepted = 'false' WHERE id = '".secure($db, $data['id'])."'");
								}
								
							}
							success('Le formulaire Guardianer a bien été traité.');
						}
						else if (isset($_POST['finish']))
						{
							$c = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(*) AS count FROM reportMsg WHERE playerTo = '".secure($db, $account['mcname'])."' AND `done` = 'false';"));
							
							if ($c['count'] == 0)
							{
								error('Vous n\'avez aucune donnée à traiter. Vous êtes par conséquent déjà en fin de service.');
							}
							else
							{
								mysqli_query($db, "UPDATE reportMsg SET playerTo = '' WHERE  playerTo = '".secure($db, $account['mcname'])."';");
								success('Fin de service. Votre formulaire va être par conséquent réattribué à un autre effectif.');
							}
						}
						
				
						$data = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM reportMsg WHERE done = 'false' AND playerTo = '".secure($db, $account['mcname'])."'"));
					
						$isWorking = $data != false && $data['count'] > 0;
						
						if (!$isWorking)
						{
							$data = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM reportMsg WHERE done = 'false' AND (playerTo IS NULL OR playerTo = '')"));
						}
					
						if (isset($_POST['startService']))
						{
							$p = mysqli_query($db, "SELECT * FROM reportMsg WHERE `done` = 'false' AND (playerTo IS NULL OR playerTo = '') ORDER BY id DESC;");
							$i = 0;
							
							while ($prt = mysqli_fetch_assoc($p))
							{
								$i++;
								if ($i > 50)
								{
									break;
								}
								mysqli_query($db, "UPDATE reportMsg SET playerTo = '".secure($db, $account['mcname'])."' WHERE id = '".secure($db, $prt['id'])."'");
							}
							
							if ($i < 50 && $i > 0)
							{
								success('Vous avez généré un formulaire de sanctions. Il y a - de 50 sanctions à traiter, il y en a seulement '.$i.'.<br />N\'oubliez pas de terminer le service quand vous arrêtez.');
							}
							else if ($i > 0)
							{
								success('Vous avez généré un formulaire complet.<br />N\'oubliez pas de terminer le service quand vous arrêtez.');
							}
							
							if ($i > 0)
							{
								// ok, now he's working!
								$isWorking = true;
								$data['count'] = $i;
							}
							else
							{
								error('Aucune donnée à traiter sur le Guardianer.');
							}
						}
					
					?>
					
						<div class="row">
						  <div class="col-sm-12">
							<div class="card-box">
								<h4 class="m-t-0 header-title"><b>Guardianer<?php if ($isWorking) echo ' ('.$data['count'].' dans le formulaire)'; else echo ' ('.$data['count'].' à traiter)'; ?></b></h4>
									
									<?php
									
										if (!$isWorking)
										{
											
									?>
									Guardianer est un système de modération en ligne permettant de gérer les signalements des joueurs ainsi que les messages signalées par l'intelligence artificielle complémentaire de Guardian.
									<br /><br />
									Le système fonctionne comme suit :
									<ul>
										<li>Vous générez un formulaire (50 messages max) & vous décidez lesquelles doivent être sanctionnées</li>
										<li>Vous <b>devez</b>, lorsque vous arrêtez de travailler, terminer le service pour redistribuer les messages à traiter aux autres membres de la modération.</li>
									</ul>
									<form method="post">
										<div class="form-group text-left m-b-0">
											<button class="btn btn-primary waves-effect waves-light" name="startService" style="width: 100%;" type="submit">
												Commencer le service
											</button>
										</div>
									</form>
									<?php
									
										}
										else
										{
									
									?>
									
									<div style="margin-top: 20px;"></div>
									
									<form method="post">
									
									<div style="margin-top: 20px;"></div>
									
									<table data-toggle="table"
                                           data-page-size="50"
                                           data-pagination="true" class="table-bordered ">
										<thead>
											<tr>
												<th data-checkbox="false"></th>
												<th>Date</th>
												<th>Pseudonyme</th>
												<th>Message</th>
											</tr>
										</thead>
										<tbody>
										
									<?php

										$request = mysqli_query($db, "SELECT * FROM reportMsg WHERE playerTo = '".secure($db, $account['mcname'])."' AND done = 'false' ORDER BY id DESC;");
											
										while ($data = mysqli_fetch_assoc($request))
										{
											
									?>
											<tr>
												<td><input type="checkbox" name="mute_<?php echo intval($data['id']);?>" /></td>
												<td><?php echo date("d/m/Y H:i:s", $data['timestamp'] / 1000); ?></td>
												<td><?php echo $data['player']; ?></td>
												<td><?php echo translateReason($data['message']); ?></td>
											</tr>
									<?php
										
										}
										
										
									?>
										</tbody>
									</table>
									<form method="post">
										<div class="form-group text-left m-b-0">
											<button class="btn btn-primary waves-effect waves-light" name="confirm" style="width: 49.5%;" type="submit">
												Valider
											</button>
                                            <button type="submit" class="btn btn-secondary waves-effect m-l-5" name="finish" style="width: 49.5%; background-color: #DF5353;">
												Annuler et Terminer le service
                                            </button>
										</div>
									</form>
								<?php
								
										}
										
									}
									
									require('includes/php/footer.php');
									
								?>
					