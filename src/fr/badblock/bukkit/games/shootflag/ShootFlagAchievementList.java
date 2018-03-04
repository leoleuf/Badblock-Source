package fr.badblock.bukkit.games.shootflag;

import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.run.BadblockGame;

public class ShootFlagAchievementList {
	public static AchievementList instance = new AchievementList(BadblockGame.SHOOTFLAG);
	
	/*
	 * Tuer X personnes
	 */
	public static final PlayerAchievement SHOOTFLAG_KILL_1 = instance.addAchievement(new PlayerAchievement("shootflag_kill_1", 10, 5, 10));
	public static final PlayerAchievement SHOOTFLAG_KILL_2 = instance.addAchievement(new PlayerAchievement("shootflag_kill_2", 50, 25, 100));
	public static final PlayerAchievement SHOOTFLAG_KILL_3 = instance.addAchievement(new PlayerAchievement("shootflag_kill_3", 250, 100, 1000));
	public static final PlayerAchievement SHOOTFLAG_KILL_4 = instance.addAchievement(new PlayerAchievement("shootflag_kill_4", 500, 250, 10000));
	
	/*
	 * Marquer X points
	 */
	public static final PlayerAchievement SHOOTFLAG_MARK_1  = instance.addAchievement(new PlayerAchievement("shootflag_mark_1", 10, 5, 5));
	public static final PlayerAchievement SHOOTFLAG_MARK_2  = instance.addAchievement(new PlayerAchievement("shootflag_mark_2", 50, 25, 500));
	public static final PlayerAchievement SHOOTFLAG_MARK_3  = instance.addAchievement(new PlayerAchievement("shootflag_mark_3", 250, 100, 5000));
	public static final PlayerAchievement SHOOTFLAG_MARK_4  = instance.addAchievement(new PlayerAchievement("shootflag_mark_4", 500, 250, 20000));

    /*
	 * Marquer 5 points en une partie sur X parties
	 */
	public static final PlayerAchievement SHOOTFLAG_MARKER_1  = instance.addAchievement(new PlayerAchievement("shootflag_marker_1", 10, 5, 5));
	public static final PlayerAchievement SHOOTFLAG_MARKER_2  = instance.addAchievement(new PlayerAchievement("shootflag_marker_2", 50, 25, 50));
	public static final PlayerAchievement SHOOTFLAG_MARKER_3  = instance.addAchievement(new PlayerAchievement("shootflag_marker_3", 250, 100, 500));
	public static final PlayerAchievement SHOOTFLAG_MARKER_4  = instance.addAchievement(new PlayerAchievement("shootflag_marker_4", 500, 250, 5000));

    
	/*
	 * Gagner X parties
	 */
	public static final PlayerAchievement SHOOTFLAG_WIN_1  = instance.addAchievement(new PlayerAchievement("shootflag_win_1", 10, 2, 1));
	public static final PlayerAchievement SHOOTFLAG_WIN_2  = instance.addAchievement(new PlayerAchievement("shootflag_win_2", 50, 25, 100));
	public static final PlayerAchievement SHOOTFLAG_WIN_3  = instance.addAchievement(new PlayerAchievement("shootflag_win_3", 250, 100, 1000));
	public static final PlayerAchievement SHOOTFLAG_WIN_4  = instance.addAchievement(new PlayerAchievement("shootflag_win_4", 500, 250, 10000));
	
	/*
	 * Tuer 10 joueurs dans une m�me partie
	 */
	public static final PlayerAchievement SHOOTFLAG_KILLER = instance.addAchievement(new PlayerAchievement("shootflag_killer", 100, 50, 10, true));
	/*
	 * Tuer 20 joueurs dans une m�me partie
	 */
	public static final PlayerAchievement SHOOTFLAG_UKILLER = instance.addAchievement(new PlayerAchievement("shootflag_ukiller", 250, 100, 25, true));

	/**
	 * Marquer 10 points dans la m�me partie
	 */
	public static final PlayerAchievement SHOOTFLAG_MARKER = instance.addAchievement(new PlayerAchievement("shootflag_umarker", 100, 50, 10, true));

	/**
	 * Tous les kits
	 */
	public static final PlayerAchievement SHOOTFLAG_ALLKITS = instance.addAchievement(new PlayerAchievement("shootflag_allkits", 300, 150, 3, true));
	
}
