package com.builtbroken.magicmirror.capability;

import com.builtbroken.magicmirror.handler.TeleportPos;

/**
 * Created by StrikerRocker on 22/6/18.
 */
public interface IMirrorData
{

    /**
     * Checks if the player has a location stored for use
     *
     * @return
     */
    boolean hasLocation();

    /**
     * Gets the location to teleport the user to
     *
     * @return
     */
    TeleportPos getLocation();

    /**
     * Sets the player's teleport location
     *
     * @param potentialTP
     */
    void setLocation(TeleportPos potentialTP);
}
