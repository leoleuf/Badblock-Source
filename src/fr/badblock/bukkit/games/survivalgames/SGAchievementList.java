package fr.badblock.bukkit.games.survivalgames;

import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.run.BadblockGame;

public class SGAchievementList {
	public static AchievementList instance = new AchievementList(BadblockGame.SURVIVAL_GAMES);
	
	/*
	 * Tuer X personnes
	 */
	public static final PlayerAchievement SG_KILL_1 = instance.addAchievement(new PlayerAchievement("sg_kill_1", 10, 5, 10));
	public static final PlayerAchievement SG_KILL_2 = instance.addAchievement(new PlayerAchievement("sg_kill_2", 50, 25, 100));
	public static final PlayerAchievement SG_KILL_3 = instance.addAchievement(new PlayerAchievement("sg_kill_3", 250, 100, 1000));
	public static final PlayerAchievement SG_KILL_4 = instance.addAchievement(new PlayerAchievement("sg_kill_4", 500, 250, 5000));
	
	/*
	 * Atteindre X foit le deathmatch
	 */
	public static final PlayerAchievement SG_SURVI_1  = instance.addAchievement(new PlayerAchievement("sg_survi_1", 10, 5, 10));
	public static final PlayerAchievement SG_SURVI_2  = instance.addAchievement(new PlayerAchievement("sg_survi_2", 50, 25, 100));
	public static final PlayerAchievement SG_SURVI_3  = instance.addAchievement(new PlayerAchievement("sg_survi_3", 250, 100, 1000));
	public static final PlayerAchievement SG_SURVI_4  = instance.addAchievement(new PlayerAchievement("sg_survi_4", 500, 250, 5000));

	/*
	 * Gagner X parties
	 */
	public static final PlayerAchievement SG_WIN_1  = instance.addAchievement(new PlayerAchievement("sg_win_1", 10, 2, 10));
	public static final PlayerAchievement SG_WIN_2  = instance.addAchievement(new PlayerAchievement("sg_win_2", 50, 25, 100));
	public static final PlayerAchievement SG_WIN_3  = instance.addAchievement(new PlayerAchievement("sg_win_3", 250, 100, 1000));
	public static final PlayerAchievement SG_WIN_4  = instance.addAchievement(new PlayerAchievement("sg_win_4", 50, 250, 5000));

	/*
	 * Gagner X parties en tuant au moins 5 joueurs
	 */
	public static final PlayerAchievement SG_FEERL_1 = instance.addAchievement(new PlayerAchievement("sg_feerl_1", 50, 25, 10));
	public static final PlayerAchievement SG_FEERL_2 = instance.addAchievement(new PlayerAchievement("sg_feerl_2", 250, 125, 100));
	public static final PlayerAchievement SG_FEERL_3 = instance.addAchievement(new PlayerAchievement("sg_feerl_3", 400, 200, 1000));
	public static final PlayerAchievement SG_FEERL_4 = instance.addAchievement(new PlayerAchievement("sg_feerl_4", 600, 350, 5000));
	
	/**
	 * Perdre 5000 parties
	 */
	public static final PlayerAchievement SG_LOOSER = instance.addAchievement(new PlayerAchievement("sg_looser", 300, 100, 5000));
	
	/*
	 * Tuer 9 joueurs dans une même partie
	 */
	public static final PlayerAchievement SG_BKILLER = instance.addAchievement(new PlayerAchievement("sg_bkiller", 300, 100, 9, true));

	/*
	 * Tuer joueurs 3 à l'arc dans une même partie
	 */
	public static final PlayerAchievement SG_SHOOTER = instance.addAchievement(new PlayerAchievement("sg_shooter", 100, 50, 3, true));
	
	/*
	 * Tuer joueurs 6 à l'arc dans une même partie
	 */
	public static final PlayerAchievement SG_USHOOTER = instance.addAchievement(new PlayerAchievement("sg_ushooter", 300, 100, 6, true));
	
	/**
	 * Tuer 9 personnes et gagner le deathmatch
	 */
	public static final PlayerAchievement SG_BSURVIVOR = instance.addAchievement(new PlayerAchievement("sg_bsurvivor", 300, 100, 1000, true));

	/**
	 * Faire deathmatch avec épée en pierre
	 */
	public static final PlayerAchievement SG_ROOKIE = instance.addAchievement(new PlayerAchievement("sg_rookie", 300, 100, 1, true));

	/**
	 * Forger une épée en diamant
	 */
	public static final PlayerAchievement SG_FORGERON = instance.addAchievement(new PlayerAchievement("sg_forgeron", 300, 100, 1, true));
	
	
	/**
	 * Frabriquer une cânne à pêche
	 */
	public static final PlayerAchievement SG_ARTISAN_FISHER = instance.addAchievement(new PlayerAchievement("sg_artisan_fisher", 100, 50, 1, true));

	/**
	 * Ne prenez pas de dégats au cours d'une partie
	 */
	public static final PlayerAchievement SG_HALF_GOD = instance.addAchievement(new PlayerAchievement("sg_half_god", 300, 100, 1, true));
	
	/**
	 * Exploser 3 lits dans une même partie
	 */
	public static final PlayerAchievement SG_ALLKITS = instance.addAchievement(new PlayerAchievement("sg_allkits", 300, 100, 3, true));
}
