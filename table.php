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
	
	if (isset($_SESSION['id-edit']))
	{
		$_GET['id'] = intval($_SESSION['id-edit']);
		unset($_SESSION['id-edit']);
	}
	
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
					
						if (!hasPermission($groups, $rank, "section.moderation.table"))
						{
							noEnoughPermissions();
						}
						else
						{
					
					?>
					
							<div class="row">
								<div class="col-sm-12">
									<h4 class="page-title">Tableaux des sanctions</h4>
									<p class="text-muted page-title-alt"></p>
								</div>
							</div>
							
								<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Tableau des sanctions en jeu</b></h4>
												<div class="row">
													<table id="tech-companies-1" class="table  table-striped">
														<thead>
															<tr>
																<th>Motif / Raison</th>
																<th>Sanction n°1</th>
																<th>Sanction n°2</th>
																<th>Sanction n°3</th>
																<th>Sanction n°4</th>
															</tr>
														</thead>
														<tbody>
															<tr>
																<td style="width: 20%;">
																	<b>Flood / Couleur</b>
																	<br /><br/>
																	(Répétition de phrases inutiles comme "aazzzazzdzda")
																</td>
																<td style="width: 20%;">
																	<b>Sommation</b>
																	<br />
																	(Il est important de prevenir une fois le joueur et de comprendre la raison de son Action)
																</td>
																<td style="width: 20%;">
																	<b>Mute 1 Heure</b>
																	<br />
																	/mute <code>&lt;pseudo&gt;</code> <b>1h Flood/Couleur</b>
																</td>
																<td style="width: 20%;">
																	Mute 3 Heures
																	<br />
																	/mute <code>&lt;pseudo&gt;</code> <b>3h Flood (2)</b>
																</td>
																<td style="width: 20%;">
																	<b>Mute 6 Heures</b>
																	<br />
																	/mute <code>&lt;pseudo&gt;</code> <b>6h Flood (3)</b>
																	<br />
																	<b><font color="red">(Ne jamais depasser cette sanction pour ce motif)</font></b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Spam</b>
																	<br /><br />
																	(Répétition d'une meme phrase)
																</td>
																<td style="width: 20%;">
																	<b>Sommation</b>
																	(Il est important de prevenir une fois le joueur et de comprendre la raison de son Action)
																</td>
																<td style="width: 20%;">
																	<b>Mute 1 Heure</b>
																	/mute <code>&lt;pseudo&gt;</code> <b>1h Spam</b>
																</td>
																<td style="width: 20%;">
																	Mute 3 Heures
																	<br />
																	/mute <code>&lt;pseudo&gt;</code> <b>3h Spam (2)</b>
																</td>
																<td style="width: 20%;">
																	<b>Mute 6 Heures</b>
																	<br />
																	/mute <code>&lt;pseudo&gt;</code> <b>6h Spam (3)</b>
																	<br />
																	<b><font color="red">(Ne jamais depasser cette sanction pour ce motif)</font></b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Propos Homophobe / Diffamation religieuse / usurpation de grade ou identité</b>
																	<br /><br />
																	Ici les propos peuvent être choquant ou poussant à une haine soit racial, religieuse, identitaire (exemple pd).
																	<br />
																	les sanctions sont plus lourdes que les autres donc soyons bon juge des situations
																</td>
																<td style="width: 20%;">
																	<b>Ban 3 jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>1d Discrimination</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 15 jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>3d Discrimination (2)</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 30 jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>30d Discrimination (3)</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Discrimination (4)</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Insulte Staff / Menace staff / Irrespect staff / Mensonge staff</b>
																	<br /><br />
																	(A CONDITION que le joueur voit que c'est un membre du staff et que l'insulte soit vraiment visé sur la fonction et non sur la personne comme "tu es un modo de merde !")
																</td>
																<td style="width: 20%;">
																	</b>Ban 7 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>7d Insulte staff</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 30 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>30d Insulte staff (2)</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Insulte staff (3)</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Anti-Jeux / Spawnkill</b>
																	<br /><br />
																	(seulement lorsque le joueur et pris sur le fait ou screen pertinent!)
																</td>
																<td style="width: 20%;">
																	<b>Ban 2 jours (Pseudo + IP)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>2d Anti-jeux</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 7 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>7d Anti-jeux (2)</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 30 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>30d Anti-jeux (3)</b>
																	<br />
																	(Ne jamais depasser cette sanction pour ce motif)"
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Insulte / Provocation / Discrimination / Citation de serveur</b>
																	<br /><br />
																	(Les insultes sont souvent inutile et dirigé vers personne en particulié et certaine sont juste issus de haine ou de degout. A vous de faire la différence entre les deux !
																	<br />
																	Il est nécessaire de se pencher sur le pourquoi de cette insulte plutôt que de faire le gendarme et mute systématiquement !
																</td>
																<td style="width: 20%;">
																	<b>Mute  1 Heure</b>
																	<br />
																	/mute <code>&lt;pseudo&gt;</code> <b>1h Insulte</b>
																</td>
																<td style="width: 20%;">
																	<b>Mute 3 Heures</b>
																	<br />
																	/mute <code>&lt;pseudo&gt;</code> <b>3h Insulte (2)</b>
																</td>
																<td style="width: 20%;">
																	Mute 6 Heures
																	<br />
																	/mute <code>&lt;pseudo&gt;</code> <b>6h Insulte (3)</b>
																	<br />
																	<b><font color="red">(Ne jamais depasser cette sanction pour ce motif)</font></b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Cheat / Aveux de Cheat + <font color="red">confirmation</font> / Refus de vérif / Déco vérif</b>
																	<br /><br />
																	(Seulement apres vérification ! Finit les bans pour cheat flagrant !)"
																</td>
																<td style="width: 20%;">
																	<b>Ban 30 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>30d Cheat (aveu)</b>
																	<br />
																	<font color="red"><b>(Si le joueur avoue et confirme)</b></font>
																	<br /><br />
																	<b>Ban 45 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>45d Cheat/Refus de vérif/Déco vérif</b>
																	<br />
																	<font color="red"><b>(Si le joueur n'avoue pas)</b></font>
																	<br /><br />
																	<b>Ban 60 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>60d Cheat / Refus de vérif / Déco vérif 
																	+ Irrespect staff</b>
																	<br />
																	<font color="red"><b>(Si le joueur fait de l'irrespect staff)</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Cheat / Déco vérif / Refus de vérif 
																	à répétition</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Menace (DDOS / Hack)</b>
																	<br /><br />
																	(suite a une phrase prouvant que la menace est bien réelle !)
																</td>
																<td style="width: 20%;">
																	<b>Ban 30 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>Menace envers joueurs</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Menace envers joueurs (2)</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Insulte Serveur / Communautée</b>
																	<br /><br />
																	(exclusivement lorsque le joueur insulte delibérément le serveur comme "Serveur de merde leurs jeux sont pourris !" ou "comunutée de merde")
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	/bban <code>&lt;pseudo&gt;</code> <b>Insulte serveur</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Publicité / Recrutement Staff</b>
																	<br /><br />
																	(exclusivement des publicité de serveurs ! Les chaînes YouTube sont autorisées sauf si la chaine est affiliée à un serveur [partenariat ou autre]).
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Pub</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Skin Diffamatoire, Injurieux ou Obscene</b>
																	<br /><br />
																	(exemple : skin d'hitler, nazi, homme nue, terroriste !)
																</td>
																<td style="width: 20%;">
																	<b>Ban 30 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>30d Skin incorrect</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Skin incorrect</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Farm AFK skyblock / Anti AFK (skyblock / faction)</b>
																	<br /><br />
																	(Après avoir appelé le joueur plusieurs fois en MP)
																</td>
																<td style="width: 20%;">
																	<b>Kick</b>
																	<br />
																	/kick <code>&lt;pseudo&gt;</code> <b>Farm AFK</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 1 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>1d Farm AFK</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 3 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>3d Farm AFK</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 7 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>7d Farm AFK</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Pillage skyblock</b>
																	<br /><br />
																	(Si le joueur parvient à prouver qu'il a recruté le joueur incriminé, la baisse
																	des niveaux, et que le joueur inciminé est bien celui qui a récupéré les items
																	(pris en flagrant délit ou aveu))
																</td>
																<td style="width: 20%;">
																	<b>Ban 30 Jours (Pseudo + Ip)</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>30d Pillage SkyBlock</b>
																	<br />
																	<b><font color="red">+ Réinitialisation de l'is, quel que soit son niveau</font></b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 6 Mois (Pseudo + Ip)</b>
																	<br />
																	<b>/btempban <code>&lt;pseudo&gt;</code> <b>180d Pillage SkyBlock</b>
																	<br />
																	<b><font color="red">+ Réinitialisation de l'is, quel que soit son niveau</font></b>
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Pillage SkyBlock</b>
																	<br />
																	<b><font color="red">+ Réinitialisation de l'is, quel que soit son niveau</font></b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Plot Obscene , religieux ou non réglementé</b>
																	<br /><br />
																	(possédant des pixelarts obscènes ou remplis exclusivement de lave et d'eau)
																	<br />
																	Attention en ce qui concerne les plot religieux il n'est pas interdit de faire une église ou une mosquée ou autre, à condition que cela ne soit pas dans l'extrême ou dans le blasphème"
																</td>
																<td style="width: 20%;">
																	<b>Ban 30 Jours (Pseudo + Ip)</b>
																	<br />
																	/btban <code>&lt;pseudo&gt;</code> <b>30d Plot Incorrect</b>
																	<br /><br />
																	& tous les helpers du plot si besoin
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Plot Incorrect</b>
																	<br /><br />
																	& tous les helpers du plot si besoin
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Sanctions multiples</b>
																	<br /><br />
																	(seulement lorsque le joueur possède des sanctions autres que MUTE et 10 sanctions minimum)
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Multiple Sanction</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Vente de compte Premium</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban Définitif (Pseudo + Ip)</b>
																	<br />
																	/bban <code>&lt;pseudo&gt;</code> <b>Vente de compte minecraft illégal</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Sanction Provisoire</b>
																</td>
																<td style="width: 20%;">
																	<b>Appliquer une sanction disponible suivant votre grade avec le motif "Sanction Provisoire"</b>
																	<br /><br />
																	Attention ! Inulte de mute quelqu'un s'il fait de l'anti-jeux ou s'il cheat, réfléchissez à votre sanction.
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Question en /modo Inutile</b>
																</td>
																<td style="width: 20%;">
																	<b>Kick<b>
																	<br />
																	/kick <code>&lt;pseudo&gt;</code> <b>Question inutile en /modo</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 1 Heure</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>1h Question inutile en /modo (2)</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 1 Jour</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>1d Question inutile en /modo (3)</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Diffamation</b>
																</td>
																<td style="width: 20%;">
																	<b>Mute 6 Heures</b>
																	<br />
																	/mute <code>&lt;pseudo&gt;</code> <b>6h Diffamation</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 1 Jour</b>
																	<br />
																	<b>/btempban <code>&lt;pseudo&gt;</code> 1d Diffamation</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 7 Jours</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>7d Diffamation</b>
																</td>
																<td style="width: 20%;">
																	<b>Ban 30 Jours</b>
																	<br />
																	/btempban <code>&lt;pseudo&gt;</code> <b>30d Diffamation</b>
																</td>
															</tr>
														</tbody>
													</table>
												</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-12">
										<div class="card-box">
											<h4 class="m-t-0 header-title"><b>Tableau des sanctions sur TeamSpeak</b></h4>
												<div class="row">
													<table id="tech-companies-1" class="table  table-striped">
														<thead>
															<tr>
																<th>Motif / Raison</th>
																<th>Sanction n°1</th>
																<th>Sanction n°2</th>
																<th>Sanction n°3</th>
																<th>Sanction n°4</th>
															</tr>
														</thead>
														<tbody>
															<tr>
																<td style="width: 20%;">
																	<b>Flood / SpamLogs</b>
																	<br /><br />
																	(Répétition de phrases inutiles comme "aazzzazzdzda") ou flood des logs
																</td>
																<td style="width: 20%;">
																	<b>Sommation</b>
																	<br />
																	(Il est important de prevenir une fois le joueur et de comprendre la raison de son Action)
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 2 heures</b>
																	<br />
																	Bannisement <b>2h Flood</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 12 heures</b>
																	<br />
																	Bannisement <b>12h Flood</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 7 jours</b>
																	<br />
																	Bannisement </b>7 jours Flood</b>
																	<br />
																	<font color="red"><b>(Ne jamais depasser cette sanction pour ce motif)</b></font>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Musique en canal public</b>
																	<br />
																	(mettre de la musique dans les channel public)
																</td>
																<td style="width: 20%;">
																	<b>Sommation</b>
																	<br />
																	(Il est important de prevenir une fois le joueur et de comprendre la raison de son Action)
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 3 heures</b>
																	<br />
																	Bannisement <b>2h Musique En Channel Public</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 12 heures</b>
																	<br />
																	Bannisement <b>12h Musique En Channel Public</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 7 jours</b>
																	<br />
																	Bannisement <b>7 jours Musique En Channel Public</b>
																	<br />
																	<font color="red"><b>(Ne jamais depasser cette sanction pour ce motif)</b></font>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Insulte/Troll</b>
																</td>
																<td style="width: 20%;">
																	<b>Sommation</b>
																	<br />
																	(Il est important de prevenir une fois le joueur et de comprendre la raison de son Action)
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 12 heures</b>
																	<br />
																	Bannisement <b>6h Insulte</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 2 Jours</b>
																	<br />
																	Bannisement <b>à vie Insulte</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 7 jours</b>
																	<br />
																	Bannisement <b>7J Insulte</b>
																	<br />
																	<font color="red"><b>(Ne jamais depasser cette sanction pour ce motif)</b></font>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Usurpation de grade</b>
																	<br /><br />
																	L'usurpation est le fait de prendre délibérément l'identité d'une autre personne  ajouter des crochets ou simplement se dire du staff
																</td>
																<td style="width: 20%;">
																	<b>Sommation</b>
																	<br />
																	(Il est important de prevenir une fois le joueur et de comprendre la raison de son Action)
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 7 jours</b>
																	<br />
																	Bannisement <b>1J Usurpation de grade</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 30 jours</b>
																	<br />
																	Bannisement <b>1J Usurpation de grade</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b>
																	<br />
																	Bannisement <b>à vie Usurpation de grade</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Propos Homophobe / Diffamation religieuse</b>
																	<br /><br />
																	Ici les propos peuvent être choquant ou poussant à une haine soit racial, religieuse, identitaire (exemple pd).
																	<br />
																	Les sanctions sont plus lourdes que les autres donc soyons bon juge des situations
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 3 jours</b>
																	<br />
																	Bannisement <b>3J Discrimination</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 7 jours</b>    
																	<br />
																	Bannisement <b>3J Discrimination</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 15 jours</b>
																	<br />
																	Bannisement <b>15J Discrimination</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b>
																	<br />
																	Bannisement à vie Discrimination
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Propos / Avatar / Pseudo obscène</b>
																	<br /><br />
																	Propos choquants ou avatar pouvant choquer
																</td>
																<td style="width: 20%;">
																	<b>Sommation</b>
																	<br />
																	(Il est important de prevenir une fois le joueur et de comprendre la raison de son Action)
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 3 jours</b>
																	<br />
																	Banniesemnt <b>3J Avatar / Avatar / Pseudo obscène</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 7 jours</b>
																	<br />
																	Bannisement <b>1J Avatar / Pseudo obscène</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b>  
																	<br />
																	Bannisement <b>à vie Avatar / Pseudo obscène</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Insulte Staff</b>
																	<br /><br />
																	(A CONDITION que le joueur voit que c'est un membre du staff et que l'insulte soit vraiment visé sur la fonction et non sur la personne comme "tu es un modo de merde !")
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 7 jours</b>
																	<br />
																	Bannisement <b>7J Insulte Staff</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 15 jours</b>
																	<br />
																	Bannisement <b>15J Insulte Staff</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b> 
																	Bannisement <b>à vie Insulte staff</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	Menace de Hack / DDoS
																	<br /><br />
																	(suite a une phrase prouvant que la menace est bien réelle)
																</td>
																<td style="width: 20%;">
																	<b>Bannissement temporaire ➠ 30 jours</b>
																	<br />
																	Bannissement <b>30J Menace de Hack</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b>
																	<br />
																	Bannisement <b>à vie Menace de Hack</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Insulte Serveur</b>
																	<br /><br />
																	(exclusivement lorsque le joueur insulte delibérément le serveur comme "Serveur de merde leurs jeux sont pourris !")
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b>
																	<br /><br />
																	Bannisement <b>à vie Insulte Serveur</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Vente de compte Premium</b>
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b>
																	<br />
																	Bannisement <b>à vie Vente de compte Premium</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Publicité / Recrutement Staff</b>
																	<br />
																	(exclusivement des publicités de serveur ! Les chaînes YouTube sont autorisées sauf si la chaine est affiliée à un serveur (partenariat ou autre))
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b>
																	<br />
																	Bannisement <b>à vie Pub</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Atteinte à la vie privée</b>
																	<br />
																	Donner des informations confidentielles ou des images de la personne concernée
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b>
																	<br />																
																	Bannisement <b>à vie Atteinte à la vie privée</b>
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>Falsification de preuves</b>
																	<br />
																	Donner une fausse preuve image modifiée
																</td>
																<td style="width: 20%;">
																	<b>Bannissement ➠ Définitif</b>
																	<br />
																	Bannisement à vie Falsification de preuves
																</td>
															</tr>
															<tr>
																<td style="width: 20%;">
																	<b>VPN</b>
																	<br /><br />
																	Les VPN sont autorisés sur BadBlock Dans le but de protéger sa vie privée si celui-ci permet de contourner une sanction alors cela est interdit
																</td>
																<td style="width: 20%;">
																	<font color="green"><b>Dans le but de protéger sa vie privée ➠ Autorisé</b></font>
																</td>
																<td style="width: 20%;">
																	<font color="red"><b>Dans un but néfaste/malveillant ➠ Bannissement définitif</b></font>
																</td>
															</tr>
														</tbody>
													</table>
												</div>
										</div>
									</div>
								</div>
							<?php
							
								}
								
								require('includes/php/footer.php');
								
							?>