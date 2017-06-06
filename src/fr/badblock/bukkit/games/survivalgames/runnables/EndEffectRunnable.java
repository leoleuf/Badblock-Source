package fr.badblock.bukkit.games.survivalgames.runnables;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.particles.ParticleData.BlockData;
import fr.badblock.gameapi.particles.ParticleEffect;
import fr.badblock.gameapi.particles.ParticleEffectType;
import fr.badblock.gameapi.players.BadblockPlayer;

public class EndEffectRunnable extends BukkitRunnable {
	private BadblockPlayer winner;
	private Random		   random	= new Random();
	
	public EndEffectRunnable(BadblockPlayer winner){
		this.winner	  = winner;
	}
	
	@Override
	public void run(){
		ParticleEffect effect = GameAPI.getAPI().createParticleEffect(ParticleEffectType.BLOCK_DUST);
		effect.setSpeed(2);
		effect.setLongDistance(true);
		effect.setAmount(100);
		effect.setData(new BlockData(Material.WOOL, color()));

		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bplayer = (BadblockPlayer) player;
			bplayer.sendParticle(winner.getLocation(), effect);
		}
	}
	
	@SuppressWarnings("deprecation")
	private byte color(){
		DyeColor[] colors = DyeColor.values();
	
		return colors[random.nextInt(colors.length)].getData();
	}
}
