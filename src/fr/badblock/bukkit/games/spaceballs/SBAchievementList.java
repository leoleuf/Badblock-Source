	package fr.badblock.bukkit.games.spaceballs;

import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.run.BadblockGame;

public class SBAchievementList {
	public static AchievementList instance = new AchievementList(BadblockGame.SPACE_BALLS);
	
	/*
	 * Tuer X personnes
	 */
	public static final PlayerAchievement SB_KILL_1 = instance.addAchievement(new PlayerAchievement("spaceballs_kill_1", 10, 5, 10));
	public static final PlayerAchievement SB_KILL_2 = instance.addAchievement(new PlayerAchievement("spaceballs_kill_2", 50, 25, 100));
	public static final PlayerAchievement SB_KILL_3 = instance.addAchievement(new PlayerAchievement("spaceballs_kill_3", 250, 100, 1000));
	public static final PlayerAchievement SB_KILL_4 = instance.addAchievement(new PlayerAchievement("spaceballs_kill_4", 500, 250, 10000));
	
	/*
	 * Poser X diamants 
	 */
	public static final PlayerAchievement SB_JEWELER_1  = instance.addAchievement(new PlayerAchievement("spaceballs_jeweler_1", 10, 5, 10));
	public static final PlayerAchievement SB_JEWELER_2  = instance.addAchievement(new PlayerAchievement("spaceballs_jeweler_2", 50, 25, 100));
	public static final PlayerAchievement SB_JEWELER_3  = instance.addAchievement(new PlayerAchievement("spaceballs_jeweler_3", 250, 100, 1000));
	public static final PlayerAchievement SB_JEWELER_4  = instance.addAchievement(new PlayerAchievement("spaceballs_jeweler_4", 500, 250, 5000));

	/*
	 * Gagner X parties
	 */
	public static final PlayerAchievement SB_WIN_1  = instance.addAchievement(new PlayerAchievement("spaceballs_win_1", 10, 5, 10));
	public static final PlayerAchievement SB_WIN_2  = instance.addAchievement(new PlayerAchievement("spaceballs_win_2", 50, 25, 100));
	public static final PlayerAchievement SB_WIN_3  = instance.addAchievement(new PlayerAchievement("spaceballs_win_3", 250, 100, 1000));
	public static final PlayerAchievement SB_WIN_4  = instance.addAchievement(new PlayerAchievement("spaceballs_win_4", 500, 250, 5000));

	/*
	 * Poser 20 diamants en une partie X fois
	 */
	public static final PlayerAchievement SB_BJEWELER_1  = instance.addAchievement(new PlayerAchievement("spaceballs_bjeweler_1", 10, 5, 10));
	public static final PlayerAchievement SB_BJEWELER_2  = instance.addAchievement(new PlayerAchievement("spaceballs_bjeweler_2", 50, 25, 100));
	public static final PlayerAchievement SB_BJEWELER_3  = instance.addAchievement(new PlayerAchievement("spaceballs_bjeweler_3", 250, 100, 1000));
	public static final PlayerAchievement SB_BJEWELER_4  = instance.addAchievement(new PlayerAchievement("spaceballs_bjeweler_4", 500, 250, 5000));
	
	/*
	 * Lancer X fusées
	 */
	public static final PlayerAchievement SB_ROCKET_1  = instance.addAchievement(new PlayerAchievement("spaceballs_rocket_1", 10, 5, 10));
	public static final PlayerAchievement SB_ROCKET_2  = instance.addAchievement(new PlayerAchievement("spaceballs_rocket_2", 50, 25, 100));
	public static final PlayerAchievement SB_ROCKET_3  = instance.addAchievement(new PlayerAchievement("spaceballs_rocket_3", 250, 100, 1000));
	public static final PlayerAchievement SB_ROCKET_4  = instance.addAchievement(new PlayerAchievement("spaceballs_rocket_4", 500, 250, 5000));

	/*
	 * Lancer 30 fusées dans une parties
	 */
	public static final PlayerAchievement SB_ROCKETER = instance.addAchievement(new PlayerAchievement("spaceballs_rocketer", 300, 100, 30, true));
	/*
	 * Poser 80 diamants dans une partie
	 */
	public static final PlayerAchievement SB_VBJEWELER = instance.addAchievement(new PlayerAchievement("spaceballs_vbjeweler", 200, 100, 80, true));

	/*
	 * Tuer 10 à l'arc joueurs dans une même partie
	 */
	public static final PlayerAchievement SB_SHOOTER = instance.addAchievement(new PlayerAchievement("spaceballs_shooter", 100, 50, 10, true));
	
	/*
	 * Tuer 20 à l'arc joueurs dans une même partie
	 */
	public static final PlayerAchievement SB_USHOOTER = instance.addAchievement(new PlayerAchievement("spaceballs_ushooter", 300, 150, 20, true));

	/**
	 * Exploser 3 lits dans une même partie
	 */
	public static final PlayerAchievement SB_ALLKITS = instance.addAchievement(new PlayerAchievement("spaceballs_allkits", 500, 200, 3, true));
}
