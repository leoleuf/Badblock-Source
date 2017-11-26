package fr.badblock.bukkit.hub.v1.tasks;

import java.util.Arrays;
import java.util.List;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.bossbars.BossBarColor;
import fr.badblock.gameapi.players.bossbars.BossBarStyle;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.Locale;

public class BossBarTask extends CustomTask {

	public int 		id;
	public float 	percent; 
	public boolean  reverse;
	
	public BossBarTask() {
		super(0, 1);
	}

	@Override
	public void done() {
		List<String> bossBar = Arrays.asList(GameAPI.i18n().get(Locale.FRENCH_FRANCE, "hub.bossbar"));
		if (!reverse && percent < 1.0f) percent += 0.01f;
		else if (reverse && percent > 0.0f) percent -= 0.01;
		else if ((reverse && percent <= 0.0f) || (!reverse && percent >= 1.0f)) {
			id++;
			reverse = !reverse;
		}
		if (percent < 0.0f) percent = 0.0f;
		else if (percent > 1.0f) percent = 1.0f;
		if (id > bossBar.size() - 1) id = 0;
		for (BadblockPlayer player : BukkitUtils.getPlayers())
			player.addBossBar("hub", player.getTranslatedMessage("hub.bossbar")[id], percent, get(player.getTranslatedMessage("hub.bossbarcolor")[id]), BossBarStyle.SOLID);
	}
	
	private BossBarColor get(String name) {
		for (BossBarColor color : BossBarColor.values())
			if (color.name().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
				return color;
		return BossBarColor.RED;
	}

}
