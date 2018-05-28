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
										<h4 class="m-t-0 header-title"><b>Double authentification</b></h4>

										<br />
											La double authentification est obligatoire (Protocole Cyan), elle n'est par conséquent <b>pas désactivable</b>.
										<br />
										
									</div>
								</div>
					</div>
					
				<?php
				
					}
					
					require('includes/php/footer.php');
					
				?>