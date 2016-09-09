package com.builtbroken.magicmirror.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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

    private World world;

    public TeleportPos(Entity e)
    {
        this(e.worldObj.provider.dimensionId, (int)e.posX, (int)e.posY, (int)e.posZ);
    }

    public TeleportPos(int dim, int x, int y, int z)
    {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
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
        player.addChatComponentMessage(new ChatComponentText("*Poof*"));
        //TODO add actual teleport code
    }
}
