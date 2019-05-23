package fr.badblock.bukkit.games.survivalgames.result;

import java.util.List;

import org.bukkit.Bukkit;

import fr.badblock.bukkit.games.survivalgames.players.SurvivalData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.result.Result;
import fr.badblock.gameapi.game.result.ResultCategoryArray;
import fr.badblock.gameapi.game.result.ResultCategoryLined;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayerData;
import fr.badblock.gameapi.players.data.InGameKitData;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import lombok.Getter;

@Getter public class SurvivalResult extends Result {
	private transient BadblockPlayer player;

	private transient ResultCategoryArray players;
	private transient ResultCategoryArray teams;
	private transient ResultCategoryLined general;

	public SurvivalResult(BadblockPlayer player) {
		super(player.getTranslatedMessage("survival.result.title", player.getName())[0]);
		this.player = player;

		general = registerCategory(CatNames.GENERAL.getName(), new ResultCategoryLined(
				get("survival.result.general.title")
				));

		players = registerCategory(CatNames.PLAYERS.getName(), new ResultCategoryArray(
				get("survival.result.players.title"),
				new String[]{
						get("survival.result.players.entry-score"),
						get("survival.result.players.entry-kills"),
						get("survival.result.players.entry-death"),
						get("survival.result.players.entry-gdamage"),
						get("survival.result.players.entry-rdamage"),
						get("survival.result.players.entry-rank"),
						get("survival.result.players.entry-kit")
				}
				));
	}

	public void doGeneral(String time, int players){
		general.addLine(get("survival.result.general.entry-date"), GameAPI.getAPI().getGameServer().getGameBegin());
		general.addLine(get("survival.result.general.entry-time"), time);
		general.addLine(get("survival.result.general.entry-server"), Bukkit.getServerName());
		general.addLine(get("survival.result.general.entry-map"), GameAPI.getAPI().getBadblockScoreboard().getWinner().getDisplayName());
		general.addLine(get("survival.result.general.entry-players"), Integer.toString(players));
	}

	private transient int pos = 1;

	public void doPlayersTop(List<BadblockPlayerData> players){
		int pos = 1;

		for(BadblockPlayerData player : players){
			String description = "[avatar:" + player.getName() + "] " + pos + " - " + player.getName();

			if(pos == 1){
				description += " [img:winner.png]";
			}

			SurvivalData  data = player.inGameData(SurvivalData.class);
			PlayerKit kit  = player.inGameData(InGameKitData.class).getChoosedKit();

			String    kitName = kit == null ? "-" : new TranslatableString("kits." + kit.getKitName() + ".itemdisplayname").getAsLine(this.player);

			this.players.addLine(description, "" + data.getScore(), "" + data.kills, data.getDeathTxt(), "" + (int) data.givedDamage,
					"" + (int) data.receivedDamage, player.getGroupPrefix().getAsLine(this.player), kitName);

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
