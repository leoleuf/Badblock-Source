package fr.badblock.bukkit.games.uhc.doublerun.players;

import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import fr.badblock.bukkit.games.uhc.doublerun.PluginUHC;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.StartRunnable;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.TeleportRunnable;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.game.GameRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.scoreboard.BadblockScoreboardGenerator;
import fr.badblock.gameapi.players.scoreboard.CustomObjective;
import fr.badblock.gameapi.utils.BorderUtils;
import fr.badblock.gameapi.utils.BukkitUtils;

public class UHCScoreboard extends BadblockScoreboardGenerator {
	private static TimeProvider timeProvider;

	private static String hoster;

	public static void setTimeProvider(TimeProvider provider){
		timeProvider = provider;

		BukkitUtils.forEachPlayers(player -> {
			if(player.getCustomObjective() == null){
				new UHCScoreboard(player);
			} else player.getCustomObjective().generate();
		});
	}

	public static final DecimalFormat df = new DecimalFormat("#");

	public static final String WINS 	  = "wins",
			KILLS 	  = "kills",
			DEATHS 	  = "deaths",
			LOOSES 	  = "looses";

	private CustomObjective objective;
	private BadblockPlayer  player;
	private boolean			endMade;
	private BukkitTask		task;

	public UHCScoreboard(BadblockPlayer player){
		String rand = UUID.randomUUID().toString().substring(0, 6);
		this.objective = GameAPI.getAPI().buildCustomObjective("udb" + rand);
		this.player    = player;

		objective.showObjective(player);
		objective.setDisplayName("§l§6» §b§lUHC DoubleRun §l§6«");
		objective.setGenerator(this);

		objective.generate();
		task = Bukkit.getScheduler().runTaskTimer(GameAPI.getAPI(), this::generate, 0, 1L);
	}

	protected boolean inGame() {
		return GameAPI.getAPI().getGameServer().getGameState() == GameState.RUNNING;
	}

	protected boolean afterGame(){
		GameState state = GameAPI.getAPI().getGameServer().getGameState();

		return state == GameState.RUNNING || state == GameState.FINISHED || state == GameState.STOPPING;
	}

	protected boolean beforeGame(){
		return GameAPI.getAPI().getGameServer().getGameState() == GameState.WAITING;
	}

	@Override
	public void generate()
	{
		if (!player.isOnline())
		{
			task.cancel();
		}
		
		int i = 16;
		String groupColor = player.getGroupPrefix().getAsLine(player);
		groupColor = groupColor.replace(ChatColor.stripColor(groupColor), "");
		i--;
		String rn = player.getRealName() != null && !player.getRealName().isEmpty() ? player.getRealName() : player.getName();
		objective.changeLine(i, "§6┌ " + groupColor + "§n" + rn);
		i--;
		objective.changeLine(i, "§6│");
		i--;
		objective.changeLine(i, "§6├§7 Rang: " + player.getGroupPrefix().getAsLine(player));

		if (beforeGame() || TeleportRunnable.teleporting)
		{
			i--;
			objective.changeLine(i, "§6├§7 Niveau: §f" + player.getPlayerData().getLevel());
			i--;
			objective.changeLine(i, "§6│");
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
			}
			i--;
			objective.changeLine(i, "§6├§7 Joueurs: §f" + BukkitUtils.getPlayers().size() + "/" + Bukkit.getMaxPlayers());
			i--;
			
			if (TeleportRunnable.teleporting)
			{
				objective.changeLine(i, "§6├§7 Téléportation en cours..");
				i--;
			}
			else if (StartRunnable.task != null)
			{
				objective.changeLine(i, "§6├§7 Phase: §fLancement...");
				i--;
				objective.changeLine(i, "§6├§7 Lancement dans §f" + time(StartRunnable.time));
			}
			else
			{
				int o = !GameAPI.getAPI().isHostedGame() ? PluginUHC.getInstance().getConfiguration().minPlayers : PluginUHC.getInstance().getMaxPlayers();
				int diff = o - BukkitUtils.getPlayers().size();
				objective.changeLine(i, "§6├§7 Phase: §fAttente joueur" + (diff > 1 ? "s" : ""));
				i--;
				objective.changeLine(i, "§6├§7 Lancement à §f" + o + " joueurs");
			}
		}
		else if (inGame())
		{
			i--;
			objective.changeLine(i, "§6│");
			
			String shownTime = "";
			String type = null;

			if(timeProvider != null){
				for(int y=0;y<timeProvider.getProvidedCount();y++){
					String id = timeProvider.getId(y);
					int  time = timeProvider.getTime(y);

					if(id == null || time < 0 || !timeProvider.displayed()) {
						continue;
					}	

					type = id;
					shownTime = time(time);
				}
			}

			if (type != null)
			{
				// TODO display le vrai type
				String nextType = "";

				if (type.equals("pve"))
				{
					type = "§aDébut NoDégât";
					nextType = "§bPréparation";
				}
				else if (type.equals("teleport"))
				{
					type = "§bPréparation";
					nextType = "§cPvP";
				}
				else if (type.equals("pvp"))
				{
					type = "§cPvP";
				}

				i--;
				objective.changeLine(i, "§6├§7 Phase: §6" + type);
				i--;
				objective.changeLine(i, "§6├§7 Temps restant: §6" + shownTime);
				i--;
				if (!type.equals("pvp"))
				{
					objective.changeLine(i, "§6├§7 avant la phase §6" + nextType);
				}
				else
				{
					objective.changeLine(i, "§6├§7 avant la fin");
				}
			}
			i--;
			objective.changeLine(i, "§6│  ");
			i--;
			objective.changeLine(i, "§6├§7 Joueurs en vie: §f" + alivePlayers() + "/" + fr.badblock.bukkit.games.uhc.doublerun.runnables.StartRunnable.PLAYERS_ON_START);
			i--;
			objective.changeLine(i, "§6├§7 Bordure: §f" + (BorderUtils.getBorderSize() / 2));
			i--;
			objective.changeLine(i, "§6├§7 Distance 0;0 : §f" + distanceFromCenter());
			i--;
		}
		else
		{
			if (!endMade)
			{
				for (int o = i; o > 0; o--)
				{
					objective.removeLine(o);
				}
				endMade = true;
			}

			i--;
			objective.changeLine(i, "§6│");
			i--;
			objective.changeLine(i, "§6├§f Fin de la partie");
			if (GameRunnable.ins != null)
			{
				if (GameRunnable.ins.winner != null)
				{
					i--;
					objective.changeLine(i, "§6│  ");
					i--;
					objective.changeLine(i, "§6├§7 Gagnants: &f" + GameRunnable.ins.winner.getChatPrefix().getAsLine(player));
				}
				else if (GameRunnable.ins.winnerPlayer != null)
				{
					i--;
					objective.changeLine(i, "§6│  ");
					i--;
					objective.changeLine(i, "§6├§7 Gagnant: §f" + GameRunnable.ins.winnerPlayer.getName());
				}
			}		
		}

		i--;
		objective.changeLine(i, "§6│  ");
		i--;
		objective.changeLine(i, "§6└ " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "IP: play.badblock.fr");
		i--;
	}

	private int alivePlayers(){
		return (int) GameAPI.getAPI().getOnlinePlayers().stream().filter(p -> !p.inGameData(UHCData.class).isDeath()).count();
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

	private String distanceFromCenter()
	{
		double dist = Math.abs(player.getLocation().getX()) + Math.abs(player.getLocation().getZ());
		return df.format(dist);
	}

	private String i18n(String key, Object... args){
		return GameAPI.i18n().get(player.getPlayerData().getLocale(), key, args)[0];
	}
}
