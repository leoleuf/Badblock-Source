package fr.badblock.bukkit.games.rush.players;

import java.util.UUID;

import org.bukkit.ChatColor;

import fr.badblock.bukkit.games.rush.entities.RushTeamData;
import fr.badblock.bukkit.games.rush.runnables.GameRunnable;
import fr.badblock.bukkit.games.rush.runnables.StartRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.scoreboard.BadblockScoreboardGenerator;
import fr.badblock.gameapi.players.scoreboard.CustomObjective;
import fr.badblock.gameapi.utils.BukkitUtils;

public class RushScoreboard extends BadblockScoreboardGenerator {
	public static final String WINS 	  = "wins",
			KILLS 	  = "kills",
			DEATHS 	  = "deaths",
			LOOSES 	  = "looses",
			BROKENBEDS = "brokenbeds";

	private static String hoster = null;
	
	private CustomObjective objective;
	private BadblockPlayer  player;

	public RushScoreboard(BadblockPlayer player){
		String rand = UUID.randomUUID().toString().substring(0, 6);
		this.objective = GameAPI.getAPI().buildCustomObjective("rush" + rand);
		this.player    = player;

		objective.showObjective(player);
		objective.setDisplayName("§l§6» §b§lRush §l§6«");
		objective.setGenerator(this);

		objective.generate();
	}

	@Override
	public void generate(){
		int i = 16;
		String groupColor = player.getGroupPrefix().getAsLine(player);
		groupColor = groupColor.replace(ChatColor.stripColor(groupColor), "");
		i--;
		objective.changeLine(i, "§6┌ " + groupColor + "&n" + player.getName());
		i--;
		objective.changeLine(i, "§6│");
		i--;
		objective.changeLine(i, "§6├§7 Rang: &f" + player.getGroupPrefix().getAsLine(player));
		i--;
		objective.changeLine(i, "§6├§7 Niveau: §f" + player.getPlayerData().getLevel());
		i--;
		
		if (GameAPI.getAPI().isHostedGame())
		{
			String by = "";
			if (hoster == null)
			{
				for (BadblockPlayer plo : BukkitUtils.getAllPlayers())
				{
					if (GameAPI.getAPI().isHoster(plo))
					{
						String realName = plo.getRealName() != null && !plo.getRealName().isEmpty() ? plo.getRealName() : plo.getName();
						hoster = realName;
						by = " par §e" + realName;
						break;
					}
				}
			}
			else
			{
				by = " par §e" + hoster;
			}

			objective.changeLine(i, "§6├§f Hosté" + by);
			i--;
		}
		
		objective.changeLine(i, "§6│");
		i--;
		BadblockTeam currentTeam = player.getTeam();
		String teamName = "§7Inconnu";
		if (currentTeam != null)
		{
			teamName = currentTeam.getChatPrefix().getAsLine(player).replace("[", "").replace("]", "");
		}
		objective.changeLine(i, "§6├§7 Map: " + GameAPI.getAPI().getBadblockScoreboard().getWinner().getDisplayName());
		i--;
		objective.changeLine(i, "§6├§7 Équipe: " + teamName);
		i--;
		
		if (StartRunnable.gameTask != null && GameRunnable.time > 0)
		{
			objective.changeLine(i, "§6├§7 En cours: §f" + time(GameRunnable.time));
		}
		else
		{
			objective.changeLine(i, "§6├§f Lancement...");
		}
		
		i--;
		objective.changeLine(i, "§6│  ");

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			RushTeamData data = team.teamData(RushTeamData.class);

			i--;
			if(data.hasBed())
				objective.changeLine(i, "§6├§f Team " + team.getChatName().getAsLine(player) + "§f> §aLit ✔");
			else
				objective.changeLine(i, "§6├§f Team " + team.getChatName().getAsLine(player) + "§f> §cLit ✘");
		}
		
		i--;
		objective.changeLine(i, "§6│");
		i--;
		objective.changeLine(i, "§6└ §bBad§6Block§f§o.fr");
		i--;
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

}
