package fr.badblock.bukkit.hub.v2.tasks.list;

import java.util.ArrayList;
import java.util.List;

import fr.badblock.bukkit.hub.v2.config.ConfigLoader;
import fr.badblock.bukkit.hub.v2.config.configs.BossBarsConfig;
import fr.badblock.bukkit.hub.v2.players.addons.BossBar;
import fr.badblock.bukkit.hub.v2.tags.TagManager;
import fr.badblock.bukkit.hub.v2.tasks.HubTask;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.bossbars.BossBarStyle;
import fr.badblock.gameapi.utils.BukkitUtils;

public class BossBarsUpdateTask extends HubTask
{

	private int 		id;
	private int			ticks;
	private boolean		reverse;
	private float		percent;

	public BossBarsUpdateTask()
	{
		super (true, 1, 1);
	}

	@Override
	public void run()
	{
		update();
	}

	private void update()
	{
		BossBarsConfig bossBarsConfig = ConfigLoader.getBossBars();

		List<BossBar> bossBars = new ArrayList<>(bossBarsConfig.getBossBars().values());
		BossBar bossBar = bossBars.get(id);

		boolean updated = false;

		if (bossBar != null)
		{
			if (ticks >= bossBar.getTicks())
			{
				id++;
				ticks = 0;
				reverse = false;
				percent = 0f;
				updated = true;
			}
			else
			{
				reverse = ticks >= bossBar.getTicks() / 2;
				ticks++;
			}
		}

		if (id + 1 > bossBars.size())
		{
			id = 0;
			updated = true;
			reverse = false;
			percent = 0f;
			ticks = 0;
		}

		bossBar = bossBars.get(id);

		show(bossBar, updated);
	}

	private void show(BossBar bossBar, boolean updated)
	{
		// Percent adjustment
		if (!reverse)
		{
			percent = (ticks - (ticks / 2)) / (Math.max(1, bossBar.getTicks()) / 2);
		}
		else if (reverse && percent > 0.0f)
		{
			percent = (Math.max(1, bossBar.getTicks()) / 2) / (ticks - (ticks / 2));
		}

		// Percent limit
		if (percent < 0.0f)
		{
			percent = 0.0f;
		}
		else if (percent > 1.0f)
		{
			percent = 1.0f;
		}

		for (BadblockPlayer player : BukkitUtils.getAllPlayers())
		{
			String line = player.getTranslatedMessage(bossBar.getI18nKey())[0];

			// Tagify
			TagManager tagManager = TagManager.getInstance();
			line = tagManager.tagify(player, line);

			// Send bossbar
			player.addBossBar("hub", line, percent, bossBar.getColor(), BossBarStyle.SOLID);
		}
	}

}