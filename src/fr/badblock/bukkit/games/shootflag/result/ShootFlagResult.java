package fr.badblock.bukkit.games.shootflag.result;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import fr.badblock.bukkit.games.shootflag.entities.ShootFlagTeamData;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagData;
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

@Getter public class ShootFlagResult extends Result {
	private transient BadblockPlayer player;

	private transient ResultCategoryArray players;
	private transient ResultCategoryArray teams;
	private transient ResultCategoryLined general;

	public ShootFlagResult(BadblockPlayer player) {
		super(player.getTranslatedMessage("shootflag.result.title", player.getName())[0]);
		this.player = player;

		general = registerCategory(CatNames.GENERAL.getName(), new ResultCategoryLined(
				get("shootflag.result.general.title")
				));

		teams = registerCategory(CatNames.TEAMS.getName(), new ResultCategoryArray(
				get("shootflag.result.teams.title"),
				new String[]{
						get("shootflag.result.teams.entry-players"),
						get("shootflag.result.teams.entry-marks"),
						get("shootflag.result.teams.entry-score")
				}
				));

		players = registerCategory(CatNames.PLAYERS.getName(), new ResultCategoryArray(
				get("shootflag.result.players.title"),
				new String[]{
						get("shootflag.result.players.entry-score"),
						get("shootflag.result.players.entry-kills"),
						get("shootflag.result.players.entry-deaths"),
						get("shootflag.result.players.entry-marks"),
						get("shootflag.result.players.entry-rank"),
						get("shootflag.result.players.entry-kit")
				}
				));
	}

	public void doGeneral(String time, int teams, int players){
		general.addLine(get("shootflag.result.general.entry-date"), GameAPI.getAPI().getGameServer().getGameBegin());
		general.addLine(get("shootflag.result.general.entry-time"), time);
		general.addLine(get("shootflag.result.general.entry-server"), Bukkit.getServerName());
		general.addLine(get("shootflag.result.general.entry-map"), GameAPI.getAPI().getBadblockScoreboard().getWinner().getDisplayName());
		general.addLine(get("shootflag.result.general.entry-teams"), Integer.toString(teams));
		general.addLine(get("shootflag.result.general.entry-players"), Integer.toString(players));
	}

	private transient int pos = 1;

	public void doTeamTop(Map<BadblockTeam, Integer> teams, BadblockTeam winner){
		teams.forEach((team, score) -> {
			String description = pos + " - " + team.getChatName().getAsLine(player);

			if(team.equals(winner)){
				description = pos + " - [img:winner.png] " + team.getChatName().getAsLine(player);
			}

			this.teams.addLine(description, StringUtils.join(team.getPlayersNameAtStart(), ", "), team.teamData(ShootFlagTeamData.class).getPoints() + "", "" + score);
			pos++;
		});
	}

	public void doPlayersTop(List<BadblockPlayerData> players){
		int pos = 1;

		for(BadblockPlayerData player : players){
			String description = "[avatar:" + player.getName() + "] " + pos + " - " + player.getName();

			if(pos == 1){
				description += " [img:winner.png]";
			}

			ShootFlagData  data = player.inGameData(ShootFlagData.class);
			PlayerKit kit  = player.inGameData(InGameKitData.class).getChoosedKit();

			String    kitName = kit == null ? "-" : new TranslatableString("kits." + kit.getKitName() + ".itemdisplayname").getAsLine(this.player);

			this.players.addLine(description, "" + data.getScore(), "" + data.kills, "" + data.deaths, "" + data.marks, 
					player.getGroupPrefix().getAsLine(this.player), kitName);

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
