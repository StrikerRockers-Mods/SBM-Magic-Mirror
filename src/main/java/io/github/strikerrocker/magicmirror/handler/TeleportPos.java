package io.github.strikerrocker.magicmirror.handler;

import io.github.strikerrocker.magicmirror.config.ConfigCost;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Stores the location to teleport the user to
 */
public class TeleportPos {
    public final int x;
    public final int y;
    public final int z;
    public final float yaw;
    public final float pitch;

    TeleportPos(Entity e) {
        this((int) e.getX(), (int) e.getY(), (int) e.getZ(), e.getYRot(), e.getXRot());
    }

    public TeleportPos(int x, int y, int z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }


    /**
     * Triggers the teleport for the user
     */
    void teleport(Player player) {
        if (player instanceof ServerPlayer) {
            player.displayClientMessage(Component.translatable("item.sbmmagicmirror:magicmirror.teleported"), true);
            ((ServerPlayer) player).connection.teleport(x + 0.5, y + 0.5, z + 0.5, yaw, pitch);
        }
    }

    /**
     * Cost in XP to teleport to the location
     */
    public float getTeleportCost(Player player) {
        if (ConfigCost.FLAT_RATE.get()) {
            return ConfigCost.XP_COST.get();
        }
        return getDistanceInt(player) * ConfigCost.XP_COST.get();
    }

    /**
     * Distance to the location from the entity
     */
    public int getDistanceInt(Entity entity) {
        return (int) Math.sqrt(Math.pow(entity.getX() - x + 0.5, 2) + Math.pow(entity.getY() - y + 0.5, 2) + Math.pow(entity.getZ() - z + 0.5, 2));
    }
}