package io.github.strikerrocker.magicmirror.handler;

import io.github.strikerrocker.magicmirror.MagicMirror;
import io.github.strikerrocker.magicmirror.capability.IMirrorData;
import io.github.strikerrocker.magicmirror.config.ConfigUse;
import io.github.strikerrocker.magicmirror.mirror.MirrorItem;
import io.github.strikerrocker.magicmirror.mirror.MirrorState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


/**
 * Stores data about the entity in order to track movement, time on surface, etc....
 */
public class EntityData {
    //Amount of time user has been on surface
    public int timeAboveGround;
    private BlockPos playerBlockPos;
    //Amount of time user has not been in sight of the sky
    private int timeWithoutSky;
    //Was user on surface last tick
    private boolean wasOnSurface;
    //Could user see sky last tick
    private boolean saySkyLastTick;
    private int timeOnSurfaceCooldown;
    private int timeWithoutSkyCooldown;
    private TeleportPos potentialTP;


    /**
     * Used to prevent the data from updating too often
     */
    private Long lastTickTime = 0L;

    private int tick = 0;

    EntityData(Entity e) {
        this((int) e.getX(), (int) e.getY(), (int) e.getZ());
    }

    private EntityData(int x, int y, int z) {
        playerBlockPos = new BlockPos(x, y, z);
    }

    /*
     *Clears data, normally called when TP location is set
     */
    void reset() {
        timeAboveGround = 0;
        timeWithoutSky = 0;
        timeOnSurfaceCooldown = 0;
        timeWithoutSkyCooldown = 0;
        potentialTP = null;
    }

    /**
     * Called to update the position data for the entity
     */
    void update(Player player, ItemStack stack) {
        Level level = player.level;
        if (player.getCapability(MagicMirror.CAPABILITY_MIRROR).isPresent()) {
            //Ensure we only update once a tick, Patch to fix if user has several mirrors in inventory
            if (!level.isClientSide() && (lastTickTime == 0 || (System.currentTimeMillis() - lastTickTime) >= 50)) {
                tick();
                playerBlockPos = player.blockPosition();
                try {
                    //Break teleport location if we get too far away
                    player.getCapability(MagicMirror.CAPABILITY_MIRROR).ifPresent(mirrorData -> {
                        if (ConfigUse.TELEPORT_BREAK_DISTANCE.get() > -1 && mirrorData.hasLocation()) {
                            if (mirrorData.getLocation().getDistanceInt(player) >= ConfigUse.TELEPORT_BREAK_DISTANCE.get()) {
                                mirrorData.setLocation(null);
                                level.playSound(null, player.blockPosition(), SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1F, 1F);
                                player.displayClientMessage(new TranslatableComponent("item.sbmmagicmirror:magicmirror.error.link.broken.distance"), true);
                            }
                        }
                    });

                    //Get current data from new position
                    final boolean isOnSurface = playerBlockPos.getY() >= level.getSeaLevel();
                    final boolean canSeeSky = level.dimensionType().hasSkyLight() && (level.canSeeSkyFromBelowWater(playerBlockPos.above()) || level.canSeeSky(playerBlockPos.above()));

                    //were we on the surface last tick and this tick
                    final boolean stillOnSurface = this.wasOnSurface && isOnSurface && saySkyLastTick && canSeeSky;

                    //Surface last tick and this tick -> increase time recorded
                    if (stillOnSurface) {
                        timeAboveGround++;
                        player.getCapability(MagicMirror.CAPABILITY_MIRROR).filter(mirrorData -> mirrorData.hasLocation() && timeAboveGround >= ConfigUse.SURFACE_COOLDOWN.get())
                                .ifPresent(mirrorData -> mirrorData.setLocation(null));
                        if (timeAboveGround == ConfigUse.MIN_SURFACE_TIME.get()) {
                            player.displayClientMessage(new TranslatableComponent("item.sbmmagicmirror:magicmirror.charged"), true);
                            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1F, 2F);
                        }
                    }
                    //if min time -> can't see sky -> no tp loc -> set loc
                    else if (timeAboveGround >= ConfigUse.MIN_SURFACE_TIME.get()) {
                        if (!canSeeSky && potentialTP == null) {
                            potentialTP = new TeleportPos(player);
                            player.getCapability(MagicMirror.CAPABILITY_MIRROR).filter(IMirrorData::hasLocation)
                                    .ifPresent(mirrorData -> mirrorData.setLocation(null));
                        }
                    }

                    //No sky -> record tp location and tick until we can set it
                    if (!canSeeSky) {
                        timeWithoutSky++;
                        if (potentialTP != null && timeWithoutSky >= ConfigUse.TP_SET_DELAY.get()) {
                            player.getCapability(MagicMirror.CAPABILITY_MIRROR).ifPresent(mirrorData -> mirrorData.setLocation(potentialTP));
                            player.displayClientMessage(new TranslatableComponent("item.sbmmagicmirror:magicmirror.location.set", potentialTP.x, potentialTP.y, potentialTP.z),
                                    true);
                            level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1F, 1F);
                            reset();
                        }
                        if (timeOnSurfaceCooldown++ >= ConfigUse.SURFACE_COOLDOWN.get()) {
                            timeAboveGround = 0;
                            timeOnSurfaceCooldown = 0;
                        }
                    } else if (timeWithoutSkyCooldown++ >= ConfigUse.SKY_COOLDOWN.get()) {
                        timeWithoutSky = 0;
                        timeWithoutSkyCooldown = 0;
                        potentialTP = null;
                    }

                    //Update data for next run
                    wasOnSurface = isOnSurface;
                    saySkyLastTick = canSeeSky;

                    if (stack.getItem() instanceof MirrorItem mirror) {
                        mirror.currentMirrorState = MirrorState.get((byte) mirror.getState(player));
                        IMirrorData mirrorData = player.getCapability(MagicMirror.CAPABILITY_MIRROR).orElse(null);
                        mirror.currentXPCostToTeleport = mirrorData.hasLocation() ? mirrorData.getLocation().getTeleportCost(player) : 0;
                    }

                } catch (Exception e) {
                    MagicMirror.logger.error("EntityData failed to update information about entity", e);
                }
                lastTickTime = System.currentTimeMillis();
            }
        }
    }

    private void tick() {
        tick++;
        if (tick + 1 >= Short.MAX_VALUE) {
            tick = 0;
        }
    }
}