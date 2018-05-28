<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);

	require_once('includes/php/logon/header.php');
	
	$dataByPage = 14;
	
	$continents = array(
		'AF' => "Afrique",
		'AN' => "Antarctique",
		'AS' => "Asie",
		'EU' => "Europe",
		'NA' => "Amérique du Nord",
		'OC' => "Océanie",
		'SA' => "Amérique du Sud"
	);
	
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
					
							$count = mysqli_fetch_assoc(mysqli_query($db, "SELECT COUNT(id) AS count FROM managerLogs WHERE mcname = '".secure($db, $account['mcname'])."' ORDER BY id DESC;"));
							$count = $count['count'];
					
					?>
					
						<div class="row">
						  <div class="col-sm-12">
							<div class="card-box">
								<h4 class="m-t-0 header-title"><b>Dernières connexions - <?php echo secure($db, $account['mcname']); ?> (<?php echo formatInt($count); ?>)</b></h4>
					
								<table id="tech-companies-1" class="table  table-striped">
									<thead>
										<tr>
											<th>Date</th>
											<th>Localisation</th>
											<th>Adresse IP de connexion</th>
											<th>Opérateur de connexion</th>
											<th>ASN de connexion</th>
											<th>IP allouée par</th>
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
								
									$request = mysqli_query($db, "SELECT * FROM managerLogs WHERE mcname = '".secure($db, $account['mcname'])."' ORDER BY id DESC LIMIT ".$firstEntry.", ".$dataByPage.";");
										
									while ($data = mysqli_fetch_assoc($request))
									{
										
										$ip = secure($db, $data['ip']);
										$record = geoip_record_by_name($ip);
										$city = utf8_encode($record['city']);
										if ($city == null)
										{
											$city = '-';
										}
										$postal_code = utf8_encode($record['postal_code']);
										if ($record['region'] != null)
										{
											$region = utf8_encode(geoip_region_name_by_code($record['country_code'], $record['region']));
										}
										else
										{
											$region = '-';
										}
										$country_name = utf8_encode($record['country_name']);
										$continent = utf8_encode(geoip_continent_code_by_name($ip));
										$isp = utf8_encode(geoip_isp_by_name($ip));
										$asn = utf8_encode(geoip_asnum_by_name($ip));
										$org = utf8_encode(geoip_org_by_name($ip));
										if (isset($continents[$continent]))
										{
											$continent = $continents[$continent];
										}
								?>
										<tr>
										  <td><?php echo secure($db, $data['date']); ?></td>
										  <td><?php echo $city.', '.$postal_code.' '.$region.', '.$country_name.', '.$continent; ?></td>
										  <td><?php echo $ip; ?></td>
										  <td><?php echo $isp; ?></td>
										  <td><?php echo $asn; ?></td>
										  <td><?php echo $org; ?></td>
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
				
					require('includes/php/footer.php');
				
				?>