package fr.badblock.bukkit.games.bedwars.players;

import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.runnables.GameRunnable;
import fr.badblock.bukkit.games.bedwars.runnables.TierRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.scoreboard.BadblockScoreboardGenerator;
import fr.badblock.gameapi.players.scoreboard.CustomObjective;

public class BedWarsScoreboard extends BadblockScoreboardGenerator {
	public static final String WINS 	  = "wins",
			KILLS 	  = "kills",
			DEATHS 	  = "deaths",
			LOOSES 	  = "looses",
			BROKENBEDS = "brokenbeds";

	private CustomObjective objective;
	private BadblockPlayer  player;

	public BedWarsScoreboard(BadblockPlayer player){
		this.objective = GameAPI.getAPI().buildCustomObjective("bedwars");
		this.player    = player;

		objective.showObjective(player);
		objective.setDisplayName("&b&o" + GameAPI.getGameName());
		objective.setGenerator(this);

		objective.generate();
		doBadblockFooter(objective);
	}

	@Override
	public void generate(){
		objective.changeLine(15, "&8&m----------------------");

		int i = 14;

		objective.changeLine(i--,  i18n("bedwars.scoreboard.time-desc"));

		String nextDiamondTier = TierRunnable.diamondTier == 1 ? "II " : TierRunnable.diamondTier == 2 ? "III " : " ";
		String nextEmeraldTier = TierRunnable.emeraldTier == 1 ? "II " : TierRunnable.emeraldTier == 2 ? "III " : " ";
		
		objective.changeLine(i--,  i18n("bedwars.scoreboard.time", time(GameRunnable.time)));
		
		objective.changeLine(i--, "");		
		
		objective.changeLine(i--,  i18n("bedwars.scoreboard.time-diamondtier", nextDiamondTier, time(TierRunnable.diamondTierTime)));
		objective.changeLine(i--,  i18n("bedwars.scoreboard.time-emeraldtier", nextEmeraldTier, time(TierRunnable.emeraldTierTime)));

		objective.changeLine(i--, "");		

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			BedWarsTeamData data = team.teamData(BedWarsTeamData.class);

			if(!data.hasBed())
				objective.changeLine(i, team.getChatName().getAsLine(player) + " > &c✘");
			else objective.changeLine(i, team.getChatName().getAsLine(player) + " > &a✔");
			i--;
		}

		for(int a=3;a<=i;a++)
			objective.removeLine(a);

		objective.changeLine(2,  "&8&m----------------------");
	}

	private String time(int time){
		if (time < 0)
		{
			return "Max";
		}
		
		String res = "m";
		int    sec = time % 60;

		res = (time / 60) + res;
		if(sec < 10){
			res += "0";
		}

		return res + sec + "s";
	}

	private String i18n(String key, Object... args){
		return GameAPI.i18n().get(player.getPlayerData().getLocale(), key, args)[0];
	}
}
