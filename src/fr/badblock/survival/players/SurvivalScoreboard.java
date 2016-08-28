package fr.badblock.survival.players;

import org.bukkit.Bukkit;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.scoreboard.BadblockScoreboardGenerator;
import fr.badblock.gameapi.players.scoreboard.CustomObjective;
import fr.badblock.gameapi.utils.BukkitUtils;

public class SurvivalScoreboard extends BadblockScoreboardGenerator {
	private static TimeProvider timeProvider;

	public static void setTimeProvider(TimeProvider provider){
		timeProvider = provider;
		
		BukkitUtils.forEachPlayers(player -> {
			if(player.getCustomObjective() == null){
				new SurvivalScoreboard(player);
			} else player.getCustomObjective().generate();
		});
	}
	
	public static final String WINS 	  = "wins",
							   KILLS 	  = "kills",
							   DEATHS 	  = "deaths",
							   LOOSES 	  = "looses";
	
	private CustomObjective objective;
	private BadblockPlayer  player;

	public SurvivalScoreboard(BadblockPlayer player){
		this.objective = GameAPI.getAPI().buildCustomObjective("survival");
		this.player    = player;

		objective.showObjective(player);
		objective.setDisplayName("&b&o" + GameAPI.getGameName());
		objective.setGenerator(this);

		objective.generate();

		doBadblockFooter(objective);
		Bukkit.getScheduler().runTaskTimer(GameAPI.getAPI(), this::doTime, 0, 20L);
	}

	public int doTime(){
		int i = 14;
		
		if(timeProvider != null){
			for(int y=0;y<timeProvider.getProvidedCount();y++){
				String id = timeProvider.getId(y);
				int  time = timeProvider.getTime(y);
			
				if(id == null)
					continue;
				
				objective.changeLine(i, i18n("survival.scoreboard.time." + id));
				objective.changeLine(i - 1, i18n("survival.scoreboard.time", time(time)));
			
				i -= 2;
			}
		}
		
		return i;
	}
	
	@Override
	public void generate(){
		objective.changeLine(15, "&8&m----------------------");

		int i = doTime();
		
		objective.changeLine(i,  i18n("survival.scoreboard.aliveplayers", alivePlayers())); i--;
		
		if(player.getBadblockMode() != BadblockMode.SPECTATOR){
			objective.changeLine(i,  ""); i--;

			objective.changeLine(i,  i18n("survival.scoreboard.wins", stat(WINS))); i--;
			objective.changeLine(i,  i18n("survival.scoreboard.kills", stat(KILLS))); i--;
			objective.changeLine(i,  i18n("survival.scoreboard.deaths", stat(DEATHS))); i--;
		}

		for(int a=3;a<=i;a++)
			objective.removeLine(a);

		objective.changeLine(2,  "&8&m----------------------");
	}
	
	private int alivePlayers(){
		return Bukkit.getOnlinePlayers().stream().mapToInt(player -> {
			BadblockPlayer p = (BadblockPlayer) player;

			return p.inGameData(SurvivalData.class).death ? 0 : 1;
		} ).sum();
	}
	
	private int stat(String name){
		return (int) player.getPlayerData().getStatistics("survival", name);
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

	private String i18n(String key, Object... args){
		return GameAPI.i18n().get(player.getPlayerData().getLocale(), key, args)[0];
	}
}
