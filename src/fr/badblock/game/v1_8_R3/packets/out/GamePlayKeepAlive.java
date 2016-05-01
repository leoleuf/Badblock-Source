package fr.badblock.game.v1_8_R3.packets.out;

import fr.badblock.game.v1_8_R3.packets.GameBadblockOutPacket;
import fr.badblock.gameapi.packets.out.play.PlayKeepAlive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutKeepAlive;

@NoArgsConstructor@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true, fluent = false)
public class GamePlayKeepAlive extends GameBadblockOutPacket implements PlayKeepAlive {
	
	private Packet<?> packet;
	
	public GamePlayKeepAlive(PacketPlayOutKeepAlive packet){
		this.packet = packet;
	}
	
	@Override
	public Packet<?> buildPacket() throws Exception {
		return packet;
	}

}
