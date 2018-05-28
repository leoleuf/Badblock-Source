<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	require_once('includes/php/database_guardian.php');
	
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
					
					if (!hasPermission($groups, $rank, "section.moderation"))
					{
						noEnoughPermissions();
					}
					else
					{
					
						if (isset($_POST['generate']))
						{
							$i = rand(10000000, 99999999);
							mysqli_query($db, "UPDATE users SET verifCode = '".secure($db, $i)."' WHERE id = '".secure($db, $account['id'])."'");
						}
						
						$cc = mysqli_fetch_assoc(mysqli_query($db, "SELECT verifCode FROM users WHERE id = '".secure($db, $account['id'])."'"));
						$code = $cc['verifCode'] == 0 ? "Veuillez générer un code" : $cc['verifCode'];
					
					?>
					
					<div class="row">
								<div class="col-sm-12">
									<div class="card-box">
										<h4 class="m-t-0 header-title"><b>Générer un code de vérification</b></h4>

										Cette page vous permet de générer rapidement un code d'accès pour l'utilisation du nouveau logiciel de vérification de BadBlock<br/><br/>
                                        Ce logiciel est privé et aucune utilisation personnelle ne sera autorisé. Détourner un logiciel de son utilisation n'est plus de la responsabilité de la personne qui l'a créé.<br/><br/>
										Procédure d'utilisation du logiciel :<br />
										<ul>
											<li>Générez un code <b>à chaque vérification</b> pour éviter que quelqu'un vous le récupère (RAT, keylogger) depuis son PC lorsque vous l'entrez !</li>
											<li>Le logiciel n'a besoin d'aucune installation. Vous devez <b>impérativement</b> le supprimer (+ la corbeille) du PC ainsi que la page de l\'historique une fois la vérification terminée !</li>
											<li>Vous devez rester en train de regarder le partage d'écran <b>TOUT LE TEMPS</b> et vérifiez vous-même l'analyse et les fichiers trouvés sur son PC !</li>
											<li>Le code temporaire se retire à chaque vérification, vous devrez par conséquent regeénérer un code à chaque vérification.</li>
										</ul><br/>
										Pour avoir accès le logiciel, il suffit de demander à vos responsables, ils auront la version à jour à chaque fois.<br/><br/>
										<form method="post">
											<input type="submit" name="generate" value="Générer un code d'accès" />
										</form>
										<b>Votre code actuel : <font style="font-size: 32px;"><?php echo secure($db, $code); ?></font></b>
									
									</div>
								</div>
					</div>
					
				<?php
				
					}
					
					require('includes/php/footer.php');
					
				?>