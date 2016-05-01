package fr.badblock.game.v1_8_R3.packets.out;

import fr.badblock.game.v1_8_R3.packets.GameBadblockOutPacket;
import fr.badblock.gameapi.packets.out.play.PlayBlockChange;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true, fluent = false)
public class GamePlayBlockChange extends GameBadblockOutPacket implements PlayBlockChange {
	
	private Packet<?> packet;
	
	public GamePlayBlockChange(PacketPlayOutBlockChange packet){
		this.packet = packet;
	}
	
	@Override
	public Packet<?> buildPacket() throws Exception {
		return packet;
	}

}
