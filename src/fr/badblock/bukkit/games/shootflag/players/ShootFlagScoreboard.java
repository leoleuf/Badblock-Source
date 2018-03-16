package fr.badblock.bukkit.games.shootflag.players;

import fr.badblock.bukkit.games.shootflag.entities.ShootFlagTeamData;
import fr.badblock.bukkit.games.shootflag.runnables.StartRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.scoreboard.BadblockScoreboardGenerator;
import fr.badblock.gameapi.players.scoreboard.CustomObjective;
import fr.badblock.gameapi.utils.ColorConverter;
import fr.badblock.gameapi.utils.general.Callback;
import fr.badblock.gameapi.utils.general.MathsUtils;

public class ShootFlagScoreboard extends BadblockScoreboardGenerator {
	public static final String WINS 	  = "wins",
			KILLS 	  = "kills",
			DEATHS 	  = "deaths",
			LOOSES 	  = "looses",
			FLAGS	  = "flags",
			SHOOTS_OK	  = "shootsok",
			SHOOTS_ERR = "shootserr";

	private CustomObjective objective;
	private BadblockPlayer  player;

	private int totalRank	= -1;
	private int monthRank	= -1;

	public ShootFlagScoreboard(BadblockPlayer player){
		this.objective = GameAPI.getAPI().buildCustomObjective("shootflag");
		this.player    = player;

		objective.showObjective(player);

		String name = "§6§l" + GameAPI.getGameName();
		if (StartRunnable.gameTask != null)
		{
			name = "&6§l" + GameAPI.getGameName() + " §d>> §b" + time(StartRunnable.gameTask.getTime());
		}

		String rankedGameName = RankedManager.instance.getCurrentRankedGameName();
		RankedManager.instance.getTotalRank(rankedGameName, player, new Callback<Integer>()
		{

			@Override
			public void done(Integer result, Throwable error) {
				totalRank = result.intValue();
			}

		});
		RankedManager.instance.getMonthRank(rankedGameName, player, new Callback<Integer>()
		{

			@Override
			public void done(Integer result, Throwable error) {
				monthRank = result.intValue();
			}

		});

		objective.setDisplayName(name);
		objective.setGenerator(this);

		objective.generate();
		doBadblockFooter(objective);
	}

	@Override
	public void generate(){
		String name = "§6§l" + GameAPI.getGameName();
		if (StartRunnable.gameTask != null)
		{
			name = "&6§l" + GameAPI.getGameName() + " §d>> §b" + time(StartRunnable.gameTask.getTime());
		}
		objective.setDisplayName(name);

		int i = 15;

		for(BadblockTeam team : GameAPI.getAPI().getTeams())
		{
			ShootFlagTeamData data = team.teamData(ShootFlagTeamData.class);
			String prefix = "";
			if (team.getOnlinePlayers().contains(player))
			{
				prefix += ColorConverter.dyeToChat(team.getDyeColor()) + "§l➔ ";
			}
			String players = "§d(" + team.getOnlinePlayers().size() + ")";
			objective.changeLine(i, prefix + team.getChatName().getAsLine(player) + players + " §8> &b" + data.getPoints());
			i--;
		}

		objective.changeLine(11,  "");
		objective.changeLine(10,  i18n("shootflag.scoreboard.monthrank", monthRank));
		objective.changeLine(9,  i18n("shootflag.scoreboard.totalrank", totalRank));
		objective.changeLine(8,  "");
		objective.changeLine(7,  i18n("shootflag.scoreboard.wins", stat(WINS)));
		objective.changeLine(6,  i18n("shootflag.scoreboard.kills", stat(KILLS), stat(DEATHS)));
		objective.changeLine(5,  i18n("shootflag.scoreboard.ratio", MathsUtils.round((double) stat(KILLS) / (double) Math.max(1, (double) stat(DEATHS)), 2))); i--;
		objective.changeLine(4,  i18n("shootflag.scoreboard.flags", stat(FLAGS)));
		double prc = (double) stat(SHOOTS_OK) / (double) Math.max(1, (double) stat(SHOOTS_OK) + (double) stat(SHOOTS_ERR)) * 100.0D;
		prc = MathsUtils.round(prc, 2);
		objective.changeLine(3,  i18n("shootflag.scoreboard.accurate", prc)); i--;

		objective.changeLine(2,  "&8&m----------------------");
	}

	private String time(int time){
		String res = "m";
		int    sec = time % 60;

		res = (time / 60) + res;
		if(sec < 10){
			res += "0";
		}

		return res + sec + "s";
	}

	private int stat(String name){
		return (int) player.getPlayerData().getStatistics("shootflag", name);
	}

	private String i18n(String key, Object... args){
		return GameAPI.i18n().get(player.getPlayerData().getLocale(), key, args)[0];
	}
}
