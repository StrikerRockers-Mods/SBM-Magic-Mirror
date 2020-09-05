package io.github.strikerrocker.magicmirror.handler;

import io.github.strikerrocker.magicmirror.MagicMirror;
import io.github.strikerrocker.magicmirror.capability.IMirrorData;
import io.github.strikerrocker.magicmirror.config.ConfigUse;
import io.github.strikerrocker.magicmirror.mirror.MirrorItem;
import io.github.strikerrocker.magicmirror.mirror.MirrorState;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;


/**
 * Stores data about the entity in order to track movement, time on surface, etc....
 */
public class EntityData {
    //Amount of time user has been on surface
    public int timeAboveGround;
    private World playerWorld;
    private BlockPos playerBlockPos;
    //Amount of time user has not been in sight of the sky
    private int timeWithoutSky;
    //Was user on surface last tick
    private boolean wasOnSurface;
    //Could user see sky last tick
    private boolean couldSeeSky;
    private int timeOnSurfaceCooldown;
    private int timeWithoutSkyCooldown;
    private TeleportPos potentialTP;


    /**
     * Used to prevent the data from updating too often
     */
    private Long lastTickTime = 0L;

    private int tick = 0;

    EntityData(Entity e) {
        this(e.world, (int) e.getPosX(), (int) e.getPosY(), (int) e.getPosZ());
    }

    private EntityData(World world, int x, int y, int z) {
        this.playerWorld = world;
        playerBlockPos = new BlockPos(x, y, z);
    }

    //Clears data, normally called when TP location is set
    void reset() {
        timeAboveGround = 0;
        timeWithoutSky = 0;
        timeOnSurfaceCooldown = 0;
        timeWithoutSkyCooldown = 0;
        potentialTP = null;
    }

    /**
     * Checks if the user is on the surface, does not
     * check if world data was set before being called
     * so can NPE
     *
     * @return true if Y level is greater than world's horizon value
     */
    private boolean checkOnSurface() {
        return playerBlockPos.getY() >= playerWorld.getSeaLevel();
    }

    /**
     * Called to update the position data for the entity
     */
    void update(PlayerEntity player, ItemStack stack) {
        //Ensure we only update once a tick, Patch to fix if user has several mirrors in inventory
        if (!player.world.isRemote && (lastTickTime == 0 || (System.currentTimeMillis() - lastTickTime) >= 50) && player.getCapability(MagicMirror.CAPABILITY_MIRROR).isPresent()) {
            tick();
            recordPosition(player);
            try {
                //Break teleport location if we get too far away
                player.getCapability(MagicMirror.CAPABILITY_MIRROR).ifPresent(mirrorData -> {
                    if (ConfigUse.TELEPORT_BREAK_DISTANCE.get() > -1 && mirrorData.hasLocation()) {
                        if (mirrorData.getLocation().getDistance(player) >= ConfigUse.TELEPORT_BREAK_DISTANCE.get()) {
                            mirrorData.setLocation(null);
                            player.sendStatusMessage(new TranslationTextComponent("item.sbmmagicmirror:magicmirror.error.link.broken.distance"), true);
                            //TODO play audio when cleared
                        }
                    }
                });

                //Get current data from new position
                final boolean isOnSurface = checkOnSurface();
                final boolean canSeeSky = checkCanSeeSky();

                //were we on the surface last tick and this tick
                final boolean stillOnSurface = this.wasOnSurface && isOnSurface && couldSeeSky && canSeeSky;

                //Surface last tick and this tick -> increase time recorded
                if (stillOnSurface) {
                    timeAboveGround++;
                    player.getCapability(MagicMirror.CAPABILITY_MIRROR).filter(mirrorData -> mirrorData.hasLocation() && timeAboveGround >= ConfigUse.SURFACE_COOLDOWN.get())
                            .ifPresent(mirrorData -> mirrorData.setLocation(null));
                    if (timeAboveGround == ConfigUse.MIN_SURFACE_TIME.get()) {
                        player.sendStatusMessage(new TranslationTextComponent("item.sbmmagicmirror:magicmirror.charged"), true);
                        //TODO Randomize message
                        //TODO add command to enable/disable message
                        //TODO play audio when charged
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
                        player.sendStatusMessage(new TranslationTextComponent("item.sbmmagicmirror:magicmirror.location.set", potentialTP.x, potentialTP.y, potentialTP.z),
                                true);
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
                couldSeeSky = canSeeSky;

                //TODO add odd effects to the mirror, for example saying its feels under used if not used after a while or lonely if the user doesn't hold it

                //If not used for a long while
                // "Mirror feels forgotten"
                // User holds it "Mirror feels loved"
                // User hovers over it "Mirror notices user's love"

                //If not held for a long while
                //"Mirror feels lonely"
                // User holds it "Mirror feels loved"
                // User hovers over it "Mirror wants to be held"

                //If user has a lot of mirrors
                //"We want to be few"
                //User holds it "I feel special"
                //User hovers over it "There can only be one"

                if (stack.getItem() instanceof MirrorItem) {
                    MirrorItem mirror1 = (MirrorItem) stack.getItem();
                    mirror1.currentMirrorState = MirrorState.get((byte) mirror1.getState(player));
                    IMirrorData mirrorData = player.getCapability(MagicMirror.CAPABILITY_MIRROR).orElse(null);
                    mirror1.currentXPCostToTeleport = mirrorData.hasLocation() ? mirrorData.getLocation().getTeleportCost(player) : 0;
                }

            } catch (Exception e) {
                if (MagicMirror.runningAsDev) {
                    MagicMirror.logger.error("EntityData failed to update information about entity", e);
                } else {
                    MagicMirror.logger.error("EntityData failed to update information about entity, errored with message: [ " + e.getMessage() + " ] enable dev mod for more detailed info.");
                }
            }
            lastTickTime = System.currentTimeMillis();
        }
    }

    private void tick() {
        tick++;
        if (tick + 1 >= Short.MAX_VALUE) {
            tick = 0;
        }
    }

    private void recordPosition(PlayerEntity player) {
        //Update position
        playerBlockPos = new BlockPos(
                (int) Math.floor(player.getPosX()),
                (int) Math.floor(player.getPosY()),
                (int) Math.floor(player.getPosZ()));
    }

    private boolean checkCanSeeSky() {
        return playerWorld.func_230315_m_().hasSkyLight() && (playerWorld.canBlockSeeSky(playerBlockPos.up()) || doRayCheckSky());
    }

    //Does a basic check to see if there is a solid block above us
    private boolean doRayCheckSky() {
        BlockPos blockPos = playerBlockPos.up();
        do {
            BlockState state = playerWorld.getBlockState(blockPos);
            if (state.getMaterial() != Material.AIR && (state.isOpaqueCube(playerWorld, blockPos))) {
                return false;
            }

            //Increase position
            blockPos = blockPos.up();
        }
        while (blockPos.getY() < 256);
        return true;
    }
}