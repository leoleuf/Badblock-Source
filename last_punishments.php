<?php

	define('secured', true);

	require_once('includes/php/functions.php');
	
	redirectIfLogOn($db);
	
	if (!hasPermission($groups, $rank, "section.moderation.overview"))
	{
		noEnoughPermissions();
		exit();
	}
	
	// Effects with lastId
	if (!isset($_SESSION['lastId']))
	{
		$_SESSION['lastId'] = 0;
	}
	
?>
<table id="tech-companies-1" class="table  table-striped">
	<thead>
		<tr>
			<th>Pseudo</th>
			<th>Date</th>
			<th>Type</th>
			<th>Par</th>
			<th>Temps</th>
			<th>Raison</th>
			<th>Preuve(s)</th>
		</tr>
	</thead>
	<tbody>
<?php

	$style = " style=\"font-size: 12px;\"";
	$request = mysqli_query($db, "SELECT id FROM sanctions ORDER BY id DESC LIMIT 1;");
	while ($data = mysqli_fetch_assoc($request)) {
		if ($_SESSION['lastId'] != $data['id'])
		{
			$style = " style=\"display: none; font-size: 12px;\"";
			$_SESSION['lastId'] = $data['id'];
			?>
			<script src="https://code.jquery.com/jquery-latest.js"></script>
			<script>
				$(document).ready(function() {
				  $.ajaxSetup({ cache: false });
				  $('#ok').fadeIn("slow");
				});
			</script>
			<?php
		}
	}
		
	$request = mysqli_query($db, "SELECT * FROM sanctions ORDER BY id DESC LIMIT 20;");
	while ($data = mysqli_fetch_assoc($request)) {
		
		$punishType = translatePunishType($data['type']);
		$punishColor = translatePunishColor($data['type']);

		// Punishment proofs?
		if ($data['proof'] != "")
		{
			$proof = make_links_clickable_proof(strip_tags(utf8_decode($data['proof']))).' — <a href="proof.php?id='.$data['id'].'">Modifier</a>';
		}
		else
		{
			$proof = "— <a href=\"proof.php?id=".$data['id']."\">Ajouter une preuve</a>";
		}
		
		$time =  ($data['expire'] - $data['timestamp']) / 1000;
		$days = floor($time / (60 * 60 * 24));
		$time -= $days * (60 * 60 * 24);
		$hours = floor($time / (60 * 60));
		$time -= $hours * (60 * 60);
		$minutes = floor($time / 60);
		$time -= $minutes * 60;
		$seconds = floor($time);
		$time -= $seconds;

		// Warn expiration
		if ($data['type'] == "warn")
		{
			$data['expire'] = -1;
		}
		
		// Time formatter
		if ($data['expire'] != -1)
		{
			$time = ($days > 0 ? $days."j" : "").($hours > 0 ? $hours."h" : "").($minutes > 0 ? $minutes."m" : "").($seconds > 0 ? $seconds."s" : "");
		}else if ($data['type'] == "ban" OR $data['type'] == "banip")
		{
			$time = "Définitif";
		}
		else
		{
			$time = "—";
		}

		$link = "modologs.php?username=".secure($db, $data['banner']);
		$banner = secure($db, $data['banner']);
		if (hasPermission($groups, $rank, "section.moderation.bymoderator"))
		{
			$banner = '<a href="modologs.php?username='.$banner.'">'.$banner.'</a>';
		}
		
		$reason = translateReason($data['reason']);
		
?>
		<tr>
			<td><a href="cases/<?php echo secure($db, $data['pseudo']); ?>"><?php echo secure($db, $data['pseudo']); ?></a></td>
			<td><?php echo secure($db, $data['date']); ?></td>
			<td><span class="label label-<?php echo $punishColor; ?>"><?php echo $punishType; ?></span></td>
			<td><?php echo $banner; ?></td>
			<td><?php echo $time; ?></td>
			<td><?php echo $reason; ?></td>
			<td><?php echo $proof; ?></td>
		</tr>
<?php
	
	}
	
?>
	</tbody>
</table>