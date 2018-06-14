package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.utils;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.disguise.Disguise;
import fr.badblock.gameapi.fakeentities.FakeEntity;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class MaterialDisguise extends Disguise {

	private Material material;
	private byte data;

	public MaterialDisguise(Material material, byte data, TranslatableString customName, boolean doWithScoreboard,
			boolean canSeeHimself) {
		super(EntityType.FALLING_BLOCK, customName, doWithScoreboard, canSeeHimself);
		this.setMaterial(material);
		this.setData(data);
	}

	@Override
	public FakeEntity<?> createFakeEntity(BadblockPlayer player) {
		return GameAPI.getAPI().spawnFakeFallingBlock(player.getLocation(), this.getMaterial(), this.getData());
	}

}