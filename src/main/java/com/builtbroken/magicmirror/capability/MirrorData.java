package com.builtbroken.magicmirror.capability;

import com.builtbroken.magicmirror.handler.TeleportPos;

/**
 * Created by StrikerRocker on 22/6/18.
 */
public class MirrorData implements IMirrorData
{
    private TeleportPos teleportPos;

    public MirrorData()
    {
    }

    @Override
    public boolean hasLocation()
    {
        return teleportPos != null;
    }

    @Override
    public TeleportPos getLocation()
    {
        return teleportPos;
    }

    @Override
    public void setLocation(TeleportPos potentialTP)
    {
        teleportPos = potentialTP;
    }
}