package io.github.strikerrocker.magicmirror.handler;

import io.github.strikerrocker.magicmirror.config.ConfigCost;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

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
        this((int) e.getPosX(), (int) e.getPosY(), (int) e.getPosZ(), e.rotationYaw, e.rotationPitch);
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
    void teleport(PlayerEntity player) {
        //TODO display particle effects as last location, and new location
        //TODO show trail point in the direction the user teleported
        //TODO use purple smoke
        //TODO play sound effect at both locations
        //TODO use different sounds for leave and enter
        if (player instanceof ServerPlayerEntity) {
            player.sendStatusMessage(new TranslationTextComponent("item.sbmmagicmirror:magicmirror.teleported"), true);
            ((ServerPlayerEntity) player).connection.setPlayerLocation(x + 0.5, y + 0.5, z + 0.5, yaw, pitch);
        }
    }

    /**
     * Cost in XP to teleport to the location
     */
    public float getTeleportCost(PlayerEntity player) {
        if (ConfigCost.FLAT_RATE.get()) {
            return ConfigCost.XP_COST.get();
        }
        return getDistanceInt(player) * ConfigCost.XP_COST.get();
    }

    /**
     * Distance to the location from the entity
     */
    private int getDistanceInt(Entity entity) {
        return (int) Math.sqrt(Math.pow(entity.getPosX() - x + 0.5, 2) + Math.pow(entity.getPosY() - y + 0.5, 2) + Math.pow(entity.getPosZ() - z + 0.5, 2));
    }

    /**
     * Distance to the location from the entity
     */
    double getDistance(Entity entity) {
        return Math.sqrt(Math.pow(entity.getPosX() - x + 0.5, 2) + Math.pow(entity.getPosY() - y + 0.5, 2) + Math.pow(entity.getPosZ() - z + 0.5, 2));
    }
}
