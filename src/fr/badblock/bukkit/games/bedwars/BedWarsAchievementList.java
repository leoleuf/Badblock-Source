package fr.badblock.bukkit.games.bedwars;

import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.run.BadblockGame;

public class BedWarsAchievementList {
	public static AchievementList instance = new AchievementList(BadblockGame.BEDWARS);
	
	/*
	 * Tuer X personnes
	 */
	public static final PlayerAchievement bedwars_KILL_1 = instance.addAchievement(new PlayerAchievement("bedwars_kill_1", 10, 5, 10));
	public static final PlayerAchievement bedwars_KILL_2 = instance.addAchievement(new PlayerAchievement("bedwars_kill_2", 50, 25, 100));
	public static final PlayerAchievement bedwars_KILL_3 = instance.addAchievement(new PlayerAchievement("bedwars_kill_3", 250, 100, 1000));
	public static final PlayerAchievement bedwars_KILL_4 = instance.addAchievement(new PlayerAchievement("bedwars_kill_4", 500, 250, 10000));
	
	/*
	 * Casser X lits
	 */
	public static final PlayerAchievement bedwars_BED_1  = instance.addAchievement(new PlayerAchievement("bedwars_bed_1", 10, 5, 5));
	public static final PlayerAchievement bedwars_BED_2  = instance.addAchievement(new PlayerAchievement("bedwars_bed_2", 50, 25, 50));
	public static final PlayerAchievement bedwars_BED_3  = instance.addAchievement(new PlayerAchievement("bedwars_bed_3", 250, 100, 500));
	public static final PlayerAchievement bedwars_BED_4  = instance.addAchievement(new PlayerAchievement("bedwars_bed_4", 500, 250, 5000));

	/*
	 * Faire exploser X lits 
	 */
	public static final PlayerAchievement bedwars_EBED_1  = instance.addAchievement(new PlayerAchievement("bedwars_ebed_1", 10, 5, 5));
	public static final PlayerAchievement bedwars_EBED_2  = instance.addAchievement(new PlayerAchievement("bedwars_ebed_2", 50, 25, 50));
	public static final PlayerAchievement bedwars_EBED_3  = instance.addAchievement(new PlayerAchievement("bedwars_ebed_3", 250, 100, 500));
	public static final PlayerAchievement bedwars_EBED_4  = instance.addAchievement(new PlayerAchievement("bedwars_ebed_4", 500, 250, 5000));

	/*
	 * Gagner X parties
	 */
	public static final PlayerAchievement bedwars_WIN_1  = instance.addAchievement(new PlayerAchievement("bedwars_win_1", 10, 2, 1));
	public static final PlayerAchievement bedwars_WIN_2  = instance.addAchievement(new PlayerAchievement("bedwars_win_2", 50, 25, 100));
	public static final PlayerAchievement bedwars_WIN_3  = instance.addAchievement(new PlayerAchievement("bedwars_win_3", 250, 100, 1000));
	public static final PlayerAchievement bedwars_WIN_4  = instance.addAchievement(new PlayerAchievement("bedwars_win_4", 500, 250, 10000));

	/*
	 * Gagner X parties en moins de 10 minutes
	 */
	public static final PlayerAchievement bedwars_RUSHER_1  = instance.addAchievement(new PlayerAchievement("bedwars_rusher_1", 10, 5, 5));
	public static final PlayerAchievement bedwars_RUSHER_2  = instance.addAchievement(new PlayerAchievement("bedwars_rusher_2", 50, 25, 50));
	public static final PlayerAchievement bedwars_RUSHER_3  = instance.addAchievement(new PlayerAchievement("bedwars_rusher_3", 250, 100, 500));
	public static final PlayerAchievement bedwars_RUSHER_4  = instance.addAchievement(new PlayerAchievement("bedwars_rusher_4", 500, 250, 5000));
	
	/*
	 * Tuer 10 joueurs dans une m�me partie
	 */
	public static final PlayerAchievement bedwars_KILLER = instance.addAchievement(new PlayerAchievement("bedwars_killer", 100, 50, 10, true));
	/*
	 * Tuer 20 joueurs dans une m�me partie
	 */
	public static final PlayerAchievement bedwars_UKILLER = instance.addAchievement(new PlayerAchievement("bedwars_ukiller", 250, 100, 25, true));

	/*
	 * Tuer 15 � l'arc joueurs dans une m�me partie
	 */
	public static final PlayerAchievement bedwars_SHOOTER = instance.addAchievement(new PlayerAchievement("bedwars_shooter", 100, 50, 15, true));
	
	/*
	 * Ne frapper les adverseraires qu'� l'arc et faire 20 kills
	 */
	public static final PlayerAchievement bedwars_USHOOTER = instance.addAchievement(new PlayerAchievement("bedwars_ushooter", 250, 150, 25, true));
	
	/**
	 * Casser 3 lits dans une m�me partie
	 */
	public static final PlayerAchievement bedwars_BROKER = instance.addAchievement(new PlayerAchievement("bedwars_broker", 100, 50, 3, true));

	/**
	 * Exploser 3 lits dans une m�me partie
	 */
	public static final PlayerAchievement bedwars_EXPLODER = instance.addAchievement(new PlayerAchievement("bedwars_exploder", 150, 75, 3, true));

	/**
	 * Exploser 3 lits dans une m�me partie
	 */
	public static final PlayerAchievement bedwars_ALLKITS = instance.addAchievement(new PlayerAchievement("bedwars_allkits", 300, 150, 3, true));
}
