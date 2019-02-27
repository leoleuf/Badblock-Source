package fr.badblock.bukkit.games.pvpbox.players;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.bukkit.potion.PotionEffectType;

import fr.badblock.api.common.utils.general.MathUtils;
import fr.badblock.bukkit.games.pvpbox.utils.LevelUtils;
import fr.badblock.game.core18R3.listeners.ChatListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.scoreboard.BadblockScoreboardGenerator;
import fr.badblock.gameapi.players.scoreboard.CustomObjective;
import fr.badblock.gameapi.run.BadblockGame;

public class BoxScoreboard extends BadblockScoreboardGenerator
{

	public static final String
	KILLS 	  = "kills",
	DEATHS 	  = "deaths",
	ASSISTS		= "assists";

	private CustomObjective objective;
	private BadblockPlayer  player;

	DecimalFormatSymbols symbols = new DecimalFormatSymbols();
	DecimalFormat goodNumberFormat1;

	public BoxScoreboard(BadblockPlayer player)
	{
		this.objective = GameAPI.getAPI().buildCustomObjective("pvpbox");
		this.player    = player;
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator(' ');
		goodNumberFormat1 = new DecimalFormat("#,##0.00#", symbols);

		objective.showObjective(player);
		objective.setDisplayName(i18n("pvpbox.scoreboard.name"));
		objective.setGenerator(this);

		objective.generate();
		doBadblockFooter(objective);
	}

	@Override
	public void generate()
	{
		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
		{
			player.setCustomNameVisible(false);
		}
		else
		{
			player.setCustomNameVisible(true);
		}
		
		int i = 15;
		objective.changeLine(15, "&8&m----------------------");
		i--;
		objective.changeLine(i,  "");

		double xp = boxPlayer.getXp();
		int currentLvl = boxPlayer.getLevel();
		double nextXp = LevelUtils.getXP(currentLvl + 1);
		
		while (xp >= nextXp)
		{
			boxPlayer.setLevel(boxPlayer.getLevel() + 1);
			currentLvl = boxPlayer.getLevel();
			nextXp = LevelUtils.getXP(currentLvl + 1);
		}
		
		ChatListener.customLevel.put(player, currentLvl);

		i--;
		objective.changeLine(i,  i18n("pvpbox.scoreboard.level", boxPlayer.getLevel(), MathUtils.round((xp / nextXp) * 100.0D, 2)));
		i--;
		long c = BadblockGame.PVPBOX.getGameData().getAchievements().getAllAchievements().stream().
				filter(ach -> player.getPlayerData().getAchievementState(ach).isSucceeds()).count();
		long t = BadblockGame.PVPBOX.getGameData().getAchievements().getAllAchievements().stream().count();
		objective.changeLine(i,  i18n("pvpbox.scoreboard.achievements", c, t));
		i--;
		objective.changeLine(i,  "");
		i--;
		objective.changeLine(i,  i18n("pvpbox.scoreboard.kills", boxPlayer.getKills()));
		i--;
		objective.changeLine(i,  i18n("pvpbox.scoreboard.assists", boxPlayer.getAssists()));
		i--;
		objective.changeLine(i,  i18n("pvpbox.scoreboard.deaths", boxPlayer.getDeaths()));
		i--;
		double rt = 0;
		if (boxPlayer.getDeaths() > 0)
		{
			rt = ((double) (double) boxPlayer.getKills() / (double) boxPlayer.getDeaths());
			rt = MathUtils.round(rt, 2);
		}
		else
		{
			rt = boxPlayer.getKills();
		}
		objective.changeLine(i,  i18n("pvpbox.scoreboard.ratio", goodNumberFormat1.format(rt)));
		i--;
		objective.changeLine(i, "");	
		i--;
		objective.changeLine(i,  i18n("pvpbox.scoreboard.ping", player.getPing()));
		i--;
		objective.changeLine(i,  "");
		i--;
		objective.changeLine(i, "&8&m----------------------");
	}

	private String i18n(String key, Object... args)
	{
		return GameAPI.i18n().get(player.getPlayerData().getLocale(), key, args)[0];
	}

}
