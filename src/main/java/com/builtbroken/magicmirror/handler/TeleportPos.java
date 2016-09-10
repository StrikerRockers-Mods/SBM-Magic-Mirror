package com.builtbroken.magicmirror.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * Stores the location to teleport the user to
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
public class TeleportPos
{
    public final int dim;
    public final int x;
    public final int y;
    public final int z;
    public final float yaw;
    public final float pitch;

    private World world;

    public TeleportPos(Entity e)
    {
        this(e.worldObj.provider.dimensionId, (int) e.posX, (int) e.posY, (int) e.posZ, e.rotationYaw, e.rotationPitch);
    }

    public TeleportPos(int dim, int x, int y, int z, float yaw, float pitch)
    {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Gets the world object
     *
     * @return world
     */
    public World world()
    {
        if (world == null)
        {
            world = DimensionManager.getWorld(dim);
        }
        return world;
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
            player.addChatComponentMessage(new ChatComponentText("*Poof*"));
            ((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(x + 0.5, y + 0.5, z + 0.5, yaw, pitch);
        }
    }

    public int getTeleportCost(EntityPlayer player)
    {
        return 0;
    }
}
