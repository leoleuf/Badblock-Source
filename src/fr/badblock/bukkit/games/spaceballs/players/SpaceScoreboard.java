package fr.badblock.bukkit.games.spaceballs.players;

import fr.badblock.bukkit.games.spaceballs.entities.SpaceTeamData;
import fr.badblock.bukkit.games.spaceballs.runnables.StartRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.scoreboard.BadblockScoreboardGenerator;
import fr.badblock.gameapi.players.scoreboard.CustomObjective;

public class SpaceScoreboard extends BadblockScoreboardGenerator {
	public static final String WINS 	  = "wins",
							   KILLS 	  = "kills",
							   DEATHS 	  = "deaths",
							   LOOSES 	  = "looses",
							   DIAMONDS   = "diamonds";
	
	private CustomObjective objective;
	private BadblockPlayer  player;

	public SpaceScoreboard(BadblockPlayer player){
		this.objective = GameAPI.getAPI().buildCustomObjective("spaceballs");
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

		int i = doTime();

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			SpaceTeamData data = team.teamData(SpaceTeamData.class);

			objective.changeLine(i, team.getChatName().getAsLine(player) + " > &a" + data.getDiamondsCount());
			i--;
		}

		if(player.getBadblockMode() != BadblockMode.SPECTATOR){
			objective.changeLine(i,  ""); i--;

			objective.changeLine(i,  i18n("spaceballs.scoreboard.wins", stat(WINS))); i--;
			objective.changeLine(i,  i18n("spaceballs.scoreboard.kills", stat(KILLS))); i--;
			objective.changeLine(i,  i18n("spaceballs.scoreboard.deaths", stat(DEATHS))); i--;
			objective.changeLine(i,  i18n("spaceballs.scoreboard.diamonds", stat(DIAMONDS))); i--;
		}

		for(int a=3;a<=i;a++)
			objective.removeLine(a);

		objective.changeLine(2,  "&8&m----------------------");
	}
	
	private int doTime(){
		int i = 14;
		
		if(StartRunnable.gameTask != null){
			objective.changeLine(i--,  i18n("spaceballs.scoreboard.time-desc"));
			objective.changeLine(i--,  i18n("spaceballs.scoreboard.time", time(StartRunnable.gameTask.getTime()) ));
			objective.changeLine(i--, "");
		}
		
		return i;
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
		return (int) player.getPlayerData().getStatistics("spaceballs", name);
	}

	private String i18n(String key, Object... args){
		return GameAPI.i18n().get(player.getPlayerData().getLocale(), key, args)[0];
	}
}
