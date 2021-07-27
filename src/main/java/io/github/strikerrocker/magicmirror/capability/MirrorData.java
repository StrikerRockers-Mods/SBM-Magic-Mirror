package io.github.strikerrocker.magicmirror.capability;

import io.github.strikerrocker.magicmirror.handler.TeleportPos;

import javax.annotation.Nullable;

public class MirrorData implements IMirrorData {
    private TeleportPos teleportPos;

    public MirrorData() {
    }

    @Override
    public boolean hasLocation() {
        return teleportPos != null && teleportPos.y > 0;
    }

    @Override
    @Nullable
    public TeleportPos getLocation() {
        return teleportPos;
    }

    @Override
    public void setLocation(TeleportPos potentialTP) {
        teleportPos = potentialTP;
    }
}