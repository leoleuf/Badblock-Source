<?php

	defined('secured') or header("Location: https://manager.badblock.fr/");
	
	if (function_exists("showVersioning"))
	{
		showVersioning();
	}
	
	minify();
		
?>
<!DOCTYPE html>
<html>

	<head>
			<meta charset="utf-8">
			<meta name="viewport" content="width=device-width, initial-scale=1.0">
			<meta name="description" content="Le Manager de BadBlock permet d'administrer les sections du serveur, de modérer ainsi que de remplir des tâches quotidiennes en relation à la vie pratique de BadBlock.">
			<meta name="author" content="xMalware">
			<link rel="shortcut icon" href="/assets/images/favicon.ico">
			<title>Manager</title>
			<link rel="stylesheet" href="/plugins/morris/morris.css">
			<link href="/assets/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
			<link href="/assets/css/icons.css" rel="stylesheet" type="text/css" />
			<link href="/assets/css/style.css" rel="stylesheet" type="text/css" />
			<script src="/assets/js/modernizr.min.js"></script>
	</head>


    <body class="fixed-left">
        <div id="wrapper">
            <div class="topbar">
                <div class="topbar-left">
                    <div class="text-center">
                        <a href="/" class="logo"><i class="icon-magnet icon-c-logo"></i><span>Manager</span></a>
                    </div>
                </div>

                <nav class="navbar-custom">
                    <ul class="list-inline float-right mb-0">

                        <li class="list-inline-item notification-list">
                            <a class="nav-link waves-light waves-effect" href="#" id="btn-fullscreen">
                                <i class="dripicons-expand noti-icon"></i>
                            </a>
                        </li>

                        <li class="list-inline-item dropdown notification-list">
                            <a class="nav-link dropdown-toggle waves-effect waves-light nav-user" data-toggle="dropdown" href="#" role="button"
                               aria-haspopup="false" aria-expanded="false">
                                <img src="http://cravatar.eu/avatar/<?php echo secure($db, $account['mcname']); ?>.png" alt="user" class="rounded-circle">
                            </a>
                            <div class="dropdown-menu dropdown-menu-right profile-dropdown " aria-labelledby="Preview">
							
                                <div class="dropdown-item noti-title">
                                    <h5 class="text-overflow" style="text-align: center;"><small><?php echo secure($db, $account['mcname']); ?>
									<br />
									<?php echo getGrade($groups, $rank); ?></small> </h5>
                                </div>
								
                                <a href="/doubleauth.php" class="dropdown-item notify-item">
                                    <i class="md md-settings"></i> <span>Double auth</span>
                                </a>
								
                                <a href="/logout.php?token=<?php echo secure($db, $_SESSION['token']); ?>" class="dropdown-item notify-item">
                                    <i class="md md-settings-power"></i> <span>Déconnexion</span>
                                </a>

                            </div>
                        </li>

                    </ul>

                    <ul class="list-inline menu-left mb-0">
                        <li class="float-left">
                            <button class="button-menu-mobile open-left waves-light waves-effect">
                                <i class="dripicons-menu"></i>
                            </button>
                        </li>
                    </ul>

                </nav>

            </div>
			
            <div class="left side-menu">
                <div class="sidebar-inner slimscrollleft">
                    <div id="sidebar-menu">
                        <ul>
                           <li><a href="/">Tableau de bord</a></li>
						<?php
						
							if (hasPermission($groups, $rank, "section.moderation"))
							{
								
						?>
                        	<li class="text-muted menu-title">Modération</li>

                            <li class="has_sub">
                                <a href="javascript:void(0);" class="waves-effect"><i class="ti-home"></i> <span> Modération </span> <span class="menu-arrow"></span></a>
                                <ul class="list-unstyled">
									<?php
									
										if (hasPermission($groups, $rank, "section.moderation.bymoderator") OR hasPermission($groups, $rank, "section.moderation.stats") OR hasPermission($groups, $rank, "section.moderation.noproofs") OR hasPermission($groups, $rank, "section.moderation.infos"))
										{
											
									?>
                                    <li class="has_sub">
										<a href="javascript:void(0);" class="waves-effect"><span> Gestion de la section </span> <span class="menu-arrow"></span></a>
										<ul class="list-unstyled">
											<?php
											
												if (hasPermission($groups, $rank, "section.moderation.absent"))
												{
											?>
											<li><a href="/mod-absents.php">Absences</a></li>
											<?php
											
												}
											
												if (hasPermission($groups, $rank, "section.moderation.regroupementmaker"))
												{
											?>
											<li><a href="/mod-regroupements.php">Regroupements</a></li>
											<?php
											
												}
												
												if (hasPermission($groups, $rank, "section.moderation.noproofs"))
												{
											?>
											<li><a href="/without_proofs.php">Sanctions sans preuves</a></li>
											<?php
												}
												
												if (hasPermission($groups, $rank, "section.moderation.bymoderator"))
												{
											?>
											<li><a href="/modo_logs.php">Logs de la modération</a></li>
											<?php
												}
												
												if (hasPermission($groups, $rank, "section.moderation.stats"))
												{
											?>
											<li><a href="/modo_stats.php">Stats des modo</a></li>
											<?php
											
												}
												
												if (hasPermission($groups, $rank, "section.moderation.infos"))
												{
											?>
											<li><a href="/modoranking.php">Classement de la modération</a></li>
											<?php
											
												}
												
											?>
										</ul>
									</li>
									<?php
									
										}
										
										if (hasPermission($groups, $rank, "section.moderation.guardian"))
										{
										
									?>
                                    <li class="has_sub">
										<a href="javascript:void(0);" class="waves-effect"><span> Vérifications </span> <span class="menu-arrow"></span></a>
										<ul class="list-unstyled">
											<?php
												if (hasPermission($groups, $rank, "section.moderation.guardian"))
												{
											?>
											<li><a href="http://discord.gg/PJAzZZq">Discord Vérifications</a></li>
											<li><a href="/verifcode.php">Code de vérif</a></li>
											<?php
												}
											?>
										</ul>
									</li>
									<?php
											
										}
										
										if (hasPermission($groups, $rank, "section.moderation.case"))
										{

									?>
                                    <li><a href="/cases">Casiers</a></li>
									<?php
									
										}
										
										if (hasPermission($groups, $rank, "section.moderation.table"))
										{
										
									?>
                                    <li><a href="/table.php">Tableaux des sanctions</a></li>
									<?php
									
										}
										
										if (hasPermission($groups, $rank, "section.moderation.multiAccountTool"))
										{
											
									?>
											<li><a href="/multiaccounttool.php">MultiAccount Tool</a></li>
									<?php
												
										}
										
										if (hasPermission($groups, $rank, "section.moderation.nicks"))
										{
										
									?>
                                    <li><a href="/nicks.php">Logs des surnoms</a></li>
									<?php
									
										}
										
										if (hasPermission($groups, $rank, "section.moderation.seehimselfstatistics"))
										{
										
									?>
                                    <li><a href="/personalstats.php">Statistiques perso</a></li>
									<?php
									
										}
												
										if (hasPermission($groups, $rank, "section.moderation.overview"))
										{
											
									?>
											<li><a href="/play_sessions.php">Sessions de jeu</a></li>
									<?php
												
										}
										
										if (hasPermission($groups, $rank, "section.moderation.guardian"))
										{
										
									?>
                                    <li><a href="/guardianlogs.php">Logs Guardian</a></li>
									<?php
									
										}
									
										if (hasPermission($groups, $rank, "section.moderation.guardianer"))
										{
										
									?>
                                    <li><a href="/guardianer.php">Guardianer</a></li>
									<?php
									
										}
									
									?>
                                </ul>
                            </li>
						<?php
						
							}
							
							if (hasPermission($groups, $rank, "section.administration"))
							{
								
						?>
                        	<li class="text-muted menu-title">Administration</li>

                            <li class="has_sub">
                                <a href="javascript:void(0);" class="waves-effect"><i class="ti-home"></i> <span> Administration </span> <span class="menu-arrow"></span></a>
                                <ul class="list-unstyled">
									<?php
										
										if (hasPermission($groups, $rank, "section.administration.warns"))
										{
										
									?>
                                    <li><a href="/admin_warns.php">Avertissements</a></li>
									<?php
									
										}
										
										if (hasPermission($groups, $rank, "section.administration.users"))
										{
										
									?>
                                    <li><a href="/admin_users.php">Gestion des utilisateurs</a></li>
									<?php
									
										}
									
									?>
                                </ul>
                            </li>
						<?php
						
							}
								
							if (hasPermission($groups, $rank, "security.doubleauth"))
							{
								
						?>
                           <li><a href="/doubleauth.php">Double authentification</a></li>
						<?php
						
							}
							
						?>
                           <li><a href="/last_logins.php">Dernières connexions</a></li>
                        </ul>
                        <div class="clearfix"></div>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </div>