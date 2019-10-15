package io.github.strikerrocker.magicmirror.capability;

import io.github.strikerrocker.magicmirror.handler.TeleportPos;

public interface IMirrorData {

    /**
     * Checks if the player has a location stored for use
     */
    boolean hasLocation();

    /**
     * Gets the location to teleport the user to
     */
    TeleportPos getLocation();

    /**
     * Sets the player's teleport location
     */
    void setLocation(TeleportPos potentialTP);
}
