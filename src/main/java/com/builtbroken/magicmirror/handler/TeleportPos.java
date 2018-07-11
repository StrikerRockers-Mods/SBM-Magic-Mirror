package com.builtbroken.magicmirror.handler;

import com.builtbroken.magicmirror.config.ConfigCost;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * Stores the location to teleport the user to
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
public class TeleportPos
{
    public final int x;
    public final int y;
    public final int z;
    public final float yaw;
    public final float pitch;

    private World world;

    public TeleportPos(Entity e)
    {
        this((int) e.posX, (int) e.posY, (int) e.posZ, e.rotationYaw, e.rotationPitch);
    }

    public TeleportPos(int x, int y, int z, float yaw, float pitch)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }


    /**
     * Triggers the teleport for the user
     *
     * @param player
     */
    public void teleport(EntityPlayer player)
    {
        //TODO display particle effects as last location, and new location
        //TODO show trail point in the direction the user teleported
        //TODO use purple smoke
        //TODO play sound effect at both locations
        //TODO use different sounds for leave and enter
        if (player instanceof EntityPlayerMP)
        {
            player.sendStatusMessage(new TextComponentTranslation("item.sbmmagicmirror:magicmirror.teleported"), true);
            ((EntityPlayerMP) player).connection.setPlayerLocation(x + 0.5, y + 0.5, z + 0.5, yaw, pitch);
        }
    }

    /**
     * Cost in XP to teleport to the location
     *
     * @param player
     * @return
     */
    public float getTeleportCost(EntityPlayer player)
    {
        if (ConfigCost.FLAT_RATE)
        {
            return ConfigCost.XP_COST;
        }
        return getDistanceInt(player) * ConfigCost.XP_COST;
    }

    /**
     * Distance to the location from the entity
     *
     * @param entity
     * @return
     */
    public int getDistanceInt(Entity entity)
    {
        return (int) Math.sqrt(Math.pow(entity.posX - x + 0.5, 2) + Math.pow(entity.posY - y + 0.5, 2) + Math.pow(entity.posZ - z + 0.5, 2));
    }

    /**
     * Distance to the location from the entity
     *
     * @param entity
     * @return
     */
    public double getDistance(Entity entity)
    {
        return Math.sqrt(Math.pow(entity.posX - x + 0.5, 2) + Math.pow(entity.posY - y + 0.5, 2) + Math.pow(entity.posZ - z + 0.5, 2));
    }
}
