package fr.badblock.bukkit.games.pvpbox;

import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.run.BadblockGame;

public class PvPBoxAchievementList
{
	
	public static AchievementList instance = new AchievementList(BadblockGame.PVPBOX);

	/*
	 * Tuer X personnes
	 */
	public static final PlayerAchievement PVPBOX_KILL_1 = instance.addAchievement(new PlayerAchievement("pvpbox_kill_1", 10, 5, 10));
	public static final PlayerAchievement PVPBOX_KILL_2 = instance.addAchievement(new PlayerAchievement("pvpbox_kill_2", 50, 25, 100));
	public static final PlayerAchievement PVPBOX_KILL_3 = instance.addAchievement(new PlayerAchievement("pvpbox_kill_3", 250, 100, 500));
	public static final PlayerAchievement PVPBOX_KILL_4 = instance.addAchievement(new PlayerAchievement("pvpbox_kill_4", 275, 150, 750));
	public static final PlayerAchievement PVPBOX_KILL_5 = instance.addAchievement(new PlayerAchievement("pvpbox_kill_5", 300, 200, 1000));
	public static final PlayerAchievement PVPBOX_KILL_6 = instance.addAchievement(new PlayerAchievement("pvpbox_kill_6", 310, 260, 2000));
	public static final PlayerAchievement PVPBOX_KILL_7 = instance.addAchievement(new PlayerAchievement("pvpbox_kill_7", 380, 320, 3000));
	public static final PlayerAchievement PVPBOX_KILL_8 = instance.addAchievement(new PlayerAchievement("pvpbox_kill_8", 460, 520, 5000));
	public static final PlayerAchievement PVPBOX_KILL_9 = instance.addAchievement(new PlayerAchievement("pvpbox_kill_9", 850, 600, 10000));
	
	/*
	 * Assists X personnes
	 */
	public static final PlayerAchievement PVPBOX_ASSISTS_1 = instance.addAchievement(new PlayerAchievement("pvpbox_assists_1", 10, 5, 10));
	public static final PlayerAchievement PVPBOX_ASSISTS_2 = instance.addAchievement(new PlayerAchievement("pvpbox_assists_2", 50, 25, 100));
	public static final PlayerAchievement PVPBOX_ASSISTS_3 = instance.addAchievement(new PlayerAchievement("pvpbox_assists_3", 250, 100, 500));
	public static final PlayerAchievement PVPBOX_ASSISTS_4 = instance.addAchievement(new PlayerAchievement("pvpbox_assists_4", 275, 150, 750));
	public static final PlayerAchievement PVPBOX_ASSISTS_5 = instance.addAchievement(new PlayerAchievement("pvpbox_assists_5", 300, 200, 1000));
	public static final PlayerAchievement PVPBOX_ASSISTS_6 = instance.addAchievement(new PlayerAchievement("pvpbox_assists_6", 310, 260, 2000));
	public static final PlayerAchievement PVPBOX_ASSISTS_7 = instance.addAchievement(new PlayerAchievement("pvpbox_assists_7", 380, 320, 3000));
	public static final PlayerAchievement PVPBOX_ASSISTS_8 = instance.addAchievement(new PlayerAchievement("pvpbox_assists_8", 460, 520, 5000));
	public static final PlayerAchievement PVPBOX_ASSISTS_9 = instance.addAchievement(new PlayerAchievement("pvpbox_assists_9", 850, 600, 10000));

	/*
	 * Tuer 10 joueurs à la suite
	 */
	public static final PlayerAchievement PVPBOX_KILLER = instance.addAchievement(new PlayerAchievement("pvpbox_killer", 500, 250, 10, true));

	/*
	 * Tuer 20 joueurs à la suite
	 */
	public static final PlayerAchievement PVPBOX_UKILLER = instance.addAchievement(new PlayerAchievement("pvpbox_ukiller", 1000, 560, 20, true));

	/*
	 * Assist 15 joueurs à la suite
	 */
	public static final PlayerAchievement PVPBOX_ASSIST = instance.addAchievement(new PlayerAchievement("pvpbox_assist", 750, 325, 15, true));
	
}
