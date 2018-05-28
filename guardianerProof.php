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
								
					if (!hasPermission($groups, $rank, "section.moderation.case"))
					{
						noEnoughPermissions();
					}
					else
					{
				
						$id = intval($_GET['id']);
						
						$request2 = mysqli_query($db, "SELECT * FROM proofGuardianer WHERE id = '".$id."'");
						$data2 = mysqli_fetch_assoc($request2);
						if ($data2 == false)
						{
							
					?>
						<div class="row">
						  <div class="col-sm-12">
							<div class="card-box">
								<h4 class="m-t-0 header-title"><b>Preuve de sanction Guardianer inconnue</b></h4>
									
									Cette preuve de sanction Guardianer n'existe pas. Faux lien ?
							</div>
						  </div>
						</div>
					
					<?php
								
						}
						else
						{
							
							?>
							<div class="row">
						  <div class="col-sm-12">
							<div class="card-box">
								<h4 class="m-t-0 header-title"><b>Preuve de sanction Guardianer inconnue</b></h4>
								<h3>Cette sanction est prouvée automatiquement par Guardianer.</h3>
								<h4><font color="red">Cette preuve est infalsifiable, le message ne peut pas être modifié.</font></h4>
								<div style="margin-left: 2%">
									<b>Pseudo:</b> <?php echo secure($db, $data2['pseudo']); ?><br />
									<b>Date:</b> <?php echo secure($db, $data2['date']); ?><br />
									<b>Sanctionné par:</b> <?php echo secure($db, $data2['banner']); ?><br />
									<b>Message envoyé par le joueur:</b> <?php echo translateReason($data2['message']); ?>
								</div>
							</div>
						  </div>
						</div>
							<?php
							
						}
										
					}
									
					require('includes/php/footer.php');
									
				?>