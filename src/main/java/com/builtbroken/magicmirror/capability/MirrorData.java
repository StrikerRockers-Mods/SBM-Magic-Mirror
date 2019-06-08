package com.builtbroken.magicmirror.capability;

import com.builtbroken.magicmirror.handler.TeleportPos;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by StrikerRocker on 22/6/18.
 */
public class MirrorData implements IMirrorData
{
    public EntityPlayer player;

    private TeleportPos teleportPos;

    public MirrorData(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public boolean hasLocation()
    {
        return teleportPos != null && teleportPos.y >= 0;
    }

    @Override
    public TeleportPos getLocation()
    {
        return teleportPos;
    }

    @Override
    public float getXpTeleportCost()
    {
        if (hasLocation())
        {
            return getLocation().getTeleportCost(player);
        }
        return 0;
    }

    @Override
    public void setLocation(TeleportPos potentialTP)
    {
        teleportPos = potentialTP;
    }
}