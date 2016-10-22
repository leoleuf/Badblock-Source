package fr.badblock.survival.players;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.badblock.gameapi.players.data.InGameData;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SurvivalData implements InGameData {
	public int 	   kills 		  = 0;
	public boolean death	      = false;
	public int     deathTime      = 0;
	public double  givedDamage    = 0;
	public double  receivedDamage = 0;
	
	public boolean canSword		  = true;
	
	public boolean zombie = false;
	
	public int getScore(){
		double ratio = (givedDamage * 5) / (receivedDamage == 0 ? 1 : receivedDamage);
		
		if(death)
			ratio /= 5;
		
		return (int) ( (kills * 5) * ratio );
	}
	
	public void checkSword(Player p){
		for(ItemStack item : p.getInventory().getContents()){
			if(ItemStackUtils.isValid(item)){
				switch(item.getType()){
					case IRON_SWORD:
					case GOLD_SWORD:
					case DIAMOND_SWORD:
						canSword = false;
						return;
					default:
				}
			}
		}
	}
	
	public String getDeathTxt(){
		if(deathTime == 0)
			return "-";
		
		String res = "m";
		int    sec = deathTime % 60;
		
		res = (deathTime / 60) + res;
		if(sec < 10){
			res += "0";
		}
		
		return res + sec + "s";
	}
	
	public int compare(SurvivalData data){
		if(!death)
			return -1;
		else if(!data.death)
			return 1;
		
		return deathTime < data.deathTime ? 1 : -1;
	}
}
