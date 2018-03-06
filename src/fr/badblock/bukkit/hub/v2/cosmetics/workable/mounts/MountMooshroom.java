package fr.badblock.bukkit.hub.v2.cosmetics.workable.mounts;

import java.lang.reflect.Field;

import org.bukkit.Location;

import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.entities.CustomCreature;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMushroomCow;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.World;

@Setter
public class MountMooshroom extends EntityMushroomCow implements IMount
{

	BadblockPlayer player;
	protected Field FIELD_JUMP = null;
	
	public MountMooshroom(World world) {
		super(world);
		if(FIELD_JUMP == null) {
			try {
				FIELD_JUMP = EntityLiving.class.getDeclaredField("aY");
				FIELD_JUMP.setAccessible(true);
			} catch(NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
    public void g(float f, float f1) {
        if(this.passenger != null && this.passenger instanceof EntityHuman)
        {
            this.lastYaw = this.yaw = this.passenger.yaw;
            this.pitch = this.passenger.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aK = this.aI = this.yaw;
            f = ((EntityLiving)this.passenger).aZ * 0.5F;
            f1 = ((EntityLiving)this.passenger).ba;
 
            if(f1 <= 0.0F)
            {
                f1 *= 0.25F;
            }
              try {
                if (FIELD_JUMP.getBoolean(this.passenger) && this.onGround) {
                    this.motY += 0.5F;
                    this.ai = true;
                    if (f1 > 0.0F)
                    {
                        float f2 = MathHelper.sin(this.yaw * 3.141593F / 180.0F);
                        float f3 = MathHelper.cos(this.yaw * 3.141593F / 180.0F);
 
                        this.motX += -0.4F * f2 * ((EntityInsentient) this).br();
                        this.motZ += 0.4F * f3 * ((EntityInsentient) this).br();
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
 
            this.S = 1.0F; this.aM = this.bI() * 0.1F; if(!this.world.isClientSide)
            {
                this.k((float)this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
                super.g(f, f1);
            }
 
            this.aA = this.aB; double d0 = this.locX - this.lastX; double d1 = this.locZ - this.lastZ; float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
            if(f4 > 1.0F)
            {
                f4 = 1.0F;
            }
 
            this.aB += (f4 - this.aB) * 0.4F; this.aC += this.aB;
        } else {
            this.S = 0.5F; this.aM = 0.02F; super.g(f, f1);
        }
    }
	
	public CustomCreature spawnEntity(Location location) 
	{
		CustomCreature creature = spawnEntity(player.getLocation());
		creature.getBukkit().setPassenger(player);
		return creature;
	}
}
