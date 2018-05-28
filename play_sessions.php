<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
	$dataByPage = 13;
	
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
					
						if (!hasPermission($groups, $rank, "section.moderation.overview"))
						{
							noEnoughPermissions();
						}
						else
						{
					
							$count = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM staffSessions WHERE playerName = '".secure($db, $account['mcname'])."' AND timestamp >= '".(strtotime('-1 month') * 1000)."' ORDER BY id DESC;"));
							$count = $count['count'];
					
					?>
					
						<div class="row">
						  <div class="col-sm-12">
							<div class="card-box">
								<h4 class="m-t-0 header-title"><b>Sessions - <?php echo secure($db, $account['mcname']); ?> (<?php echo formatInt($count); ?>)</b></h4>
					
								<table id="tech-companies-1" class="table  table-striped">
									<thead>
										<tr>
											<th>Début / Fin</th>
											<th>Temps de connexion</th>
											<th>% de modération</th>
										</tr>
									</thead>
									<tbody>
									
								<?php

									$pageNumber = ceil($count / $dataByPage);
									
									if (isset($_GET['page']))
									{
										$page = intval($_GET['page']);
										
										if ($page > $pageNumber)
										{
											$page = $pageNumber;
										}
										else if ($page < 1)
										{
											$page = 1;
										}
										
									}
									else
									{
										$page = 1;
									}
									
									$firstEntry = ($page - 1) * $dataByPage;
								
									$request = mysqli_query($db, "SELECT * FROM staffSessions WHERE playerName = '".secure($db, $account['mcname'])."' AND timestamp >= '".(strtotime('-1 month') * 1000)."' ORDER BY id DESC LIMIT ".$firstEntry.", ".$dataByPage.";");
										
									while ($data = mysqli_fetch_assoc($request))
									{
										
										if ($data['totalTime'] != 0)
										{
											$percent = round(($data['sanctionsTime'] / $data['totalTime']) * 100, 2);
										}
										else
										{
											$percent = 0;
										}
										
								?>
										<tr>
											<td><?php echo secure($db, date("d/m/Y H:i:s", $data['startTime'] / 1000)).' / '.secure($db, date("d/m/Y H:i:s", $data['endTime'] / 1000)); ?></td>
											<td><?php echo format_time($data['totalTime']); ?></td>
											<td><?php echo format($percent); ?> %</td>
										</tr>
								<?php
									
									}
									
								?>
									</tbody>
								</table>
								<form method="get" class="input-group" style="float:right;">
								  <?php
									for ($i = 1; $i <= $pageNumber; $i++)
									{
										if (abs($page - $i) <= 1 || $i == 1 || $i == $pageNumber)
										{
											echo '<input type="submit" class="btn btn-sm btn-default" value="'.$i.'" name="page" />&nbsp;&nbsp;&nbsp;';
										}
									}
								  ?>
								</form>
								<div style="margin-top: 30px;"></div>
						   </div>
						</div>
                  </div> <!-- container -->
				  <?php
				  
						}
				
						require('includes/php/footer.php');
						
					?>