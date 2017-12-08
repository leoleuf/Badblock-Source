package fr.badblock.bukkit.games.bedwars.result;

import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.players.BedWarsData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.result.Result;
import fr.badblock.gameapi.game.result.ResultCategoryArray;
import fr.badblock.gameapi.game.result.ResultCategoryLined;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayerData;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.InGameKitData;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.utils.general.StringUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
@Getter
public class BedWarsResult extends Result {
	private transient BadblockPlayer player;
	private transient ResultCategoryArray players;
	private transient ResultCategoryArray teams;
	private transient ResultCategoryLined general;

	public BedWarsResult(BadblockPlayer player) {
		super(player.getTranslatedMessage("bedwars.result.title", player.getName())[0]);
		this.player = player;
		general = registerCategory(CatNames.GENERAL.getName(), new ResultCategoryLined(get("bedwars.result.general.title")));
		teams = registerCategory(CatNames.TEAMS.getName(), new ResultCategoryArray(get("bedwars.result.teams.title"), new String[]{get("bedwars.result.teams.entry-players"), get("bedwars.result.teams.entry-bed"), get("bedwars.result.teams.entry-score")}));
		players = registerCategory(CatNames.PLAYERS.getName(), new ResultCategoryArray(get("bedwars.result.players.title"), new String[]{get("bedwars.result.players.entry-score"), get("bedwars.result.players.entry-kills"), get("bedwars.result.players.entry-deaths"), get("bedwars.result.players.entry-beds"), get("bedwars.result.players.entry-rank"), get("bedwars.result.players.entry-kit")}));
	}

	public void doGeneral(String time, int teams, int players){
		general.addLine(get("bedwars.result.general.entry-date"), GameAPI.getAPI().getGameServer().getGameBegin());
		general.addLine(get("bedwars.result.general.entry-time"), time);
		general.addLine(get("bedwars.result.general.entry-server"), Bukkit.getServerName());
		general.addLine(get("bedwars.result.general.entry-map"), GameAPI.getAPI().getBadblockScoreboard().getWinner().getDisplayName());
		general.addLine(get("bedwars.result.general.entry-teams"), Integer.toString(teams));
		general.addLine(get("bedwars.result.general.entry-players"), Integer.toString(players));
	}

	private transient int pos = 1;

	public void doTeamTop(Map<BadblockTeam, Integer> teams, BadblockTeam winner){
		teams.forEach((team, score) -> {
			String description = pos + " - " + team.getChatName().getAsLine(player);
			if(team.equals(winner)) description = pos + " - [img:winner.png] " + team.getChatName().getAsLine(player);
			this.teams.addLine(description, StringUtils.join(team.getPlayersNameAtStart(), ", "), team.teamData(BedWarsTeamData.class).getBed().getAsLine(player), "" + score);
			pos++;
		});
	}

	public void doPlayersTop(List<BadblockPlayerData> players){
		int pos = 1;

		for(BadblockPlayerData player : players){
			String description = "[avatar:" + player.getName() + "] " + pos + " - " + player.getName();
			if(pos == 1) description += " [img:winner.png]";
			BedWarsData  data = player.inGameData(BedWarsData.class);
			PlayerKit kit  = player.inGameData(InGameKitData.class).getChoosedKit();
			String kitName = kit == null ? "-" : new TranslatableString("kits." + kit.getKitName() + ".itemdisplayname").getAsLine(this.player);
			this.players.addLine(description, "" + data.getScore(), "" + data.kills, "" + data.deaths, "" + data.brokedBeds, player.getGroupPrefix().getAsLine(this.player), kitName);
			pos++;
		}
	}

	private String get(String key, Object... args){
		return player.getTranslatedMessage(key, args)[0];
	}

	public enum CatNames {
		GENERAL("general"),
		TEAMS("teams"),
		PLAYERS("players");

		@Getter
		private String name;

		CatNames(String name){
			this.name = name;
		}
	}
}
