package com.builtbroken.magicmirror.handler;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.capability.MirrorStorage;
import com.builtbroken.magicmirror.config.ConfigUse;
import com.builtbroken.magicmirror.mirror.ItemMirror;
import com.builtbroken.magicmirror.mirror.MirrorState;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;


/**
 * Stores data about the entity in order to track movement, time on surface, etc....
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/9/2016.
 */
public class EntityData
{
    //Last tick location data
    public World playerWorld;
    public BlockPos playerBlockPos;

    /**
     * Amount of time user has been on surface
     */
    public int timeAboveGround;
    /**
     * Amount of time user has not been in sight of the sky
     */
    public int timeWithoutSky;
    /**
     * Was user on surface last tick
     */
    public boolean wasOnSurface;
    /**
     * Could user see sky last tick
     */
    public boolean couldSeeSky;
    private int timeOnSurfaceCooldown;
    private int timeWithoutSkyCooldown;
    private TeleportPos potentialTP;


    /**
     * Used to prevent the data from updating too often
     */
    private Long lastTickTime = 0L;

    private int tick = 0;

    public EntityData(Entity e)
    {
        this(e.world, (int) e.posX, (int) e.posY, (int) e.posZ);
    }

    public EntityData(World world, int x, int y, int z)
    {
        this.playerWorld = world;
        playerBlockPos = new BlockPos(x, y, z);
    }

    //Clears data, normally called when TP location is set
    public void reset()
    {
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
    public boolean checkOnSurface()
    {
        return playerBlockPos.getY() >= playerWorld.getDimension().getHorizon();
    }

    /**
     * Called to update the position data for the entity
     *
     * @param player - entity to user coords for
     */
    public void update(EntityPlayer player, ItemStack stack)
    {
        //Ensure we only update once a tick, Patch to fix if user has several mirrors in inventory
        if (!player.world.isRemote && (lastTickTime == 0 || (System.currentTimeMillis() - lastTickTime) >= 50) && MirrorHandler.getData(player) != null) {
            tick();
            recordPosition(player);
            try {
                //Break teleport location if we get too far away
                if (ConfigUse.TELEPORT_BREAK_DISTANCE.get() > -1 && MirrorHandler.getData(player).hasLocation()) {
                    TeleportPos pos = MirrorHandler.getData(player).getLocation();
                    double distance = pos.getDistance(player);
                    if (distance >= ConfigUse.TELEPORT_BREAK_DISTANCE.get()) {
                        player.getCapability(MirrorStorage.CAPABILITY_MIRROR).ifPresent(iMirrorData -> iMirrorData.setLocation(null));
                        player.sendStatusMessage(new TextComponentTranslation("item.sbmmagicmirror:magicmirror.error.link.broken.distance"), true);
                        //TODO play audio when cleared
                    }
                }

                //Get current data from new position
                final boolean isOnSurface = checkOnSurface();
                final boolean canSeeSky = checkCanSeeSky();

                //were we on the surface last tick and this tick
                final boolean stillOnSurface = this.wasOnSurface && isOnSurface && couldSeeSky && canSeeSky;

                //Surface last tick and this tick -> increase time recorded
                if (stillOnSurface) {
                    timeAboveGround++;
                    if (MirrorHandler.getData(player).hasLocation() && timeAboveGround >= ConfigUse.SURFACE_COOLDOWN.get()) {
                        MirrorHandler.getData(player).setLocation(null);
                    }
                    if (timeAboveGround == ConfigUse.MIN_SURFACE_TIME.get()) {
                        player.sendStatusMessage(new TextComponentTranslation("item.sbmmagicmirror:magicmirror.charged"), true);
                        //TODO Randomize message
                        //TODO add command to enable/disable message
                        //TODO play audio when charged
                    }
                }
                //if min time -> can't see sky -> no tp loc -> set loc
                else if (timeAboveGround >= ConfigUse.MIN_SURFACE_TIME.get()) {
                    if (!canSeeSky && potentialTP == null) {
                        potentialTP = new TeleportPos(player);
                        if (MirrorHandler.getData(player).hasLocation()) {
                            MirrorHandler.getData(player).setLocation(null);
                        }
                    }
                }

                //No sky -> record tp location and tick until we can set it
                if (!canSeeSky) {
                    timeWithoutSky++;
                    if (potentialTP != null && timeWithoutSky >= ConfigUse.TP_SET_DELAY.get()) {
                        MirrorHandler.getData(player).setLocation(potentialTP);
                        player.sendStatusMessage(new TextComponentTranslation(
                                        "item.sbmmagicmirror:magicmirror.location.set",
                                        potentialTP.x, potentialTP.y, potentialTP.z),
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

                if (stack.getItem() instanceof ItemMirror) {
                    ItemMirror mirror1 = (ItemMirror) stack.getItem();
                    mirror1.currentMirrorState = MirrorState.get((byte) mirror1.getState(player));
                    mirror1.currentXPCostToTeleport = MirrorHandler.getData(player).hasLocation() ? MirrorHandler.getData(player).getLocation().getTeleportCost(player) : 0;
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

    private void tick()
    {
        tick++;
        if (tick + 1 >= Short.MAX_VALUE) {
            tick = 0;
        }
    }

    private void recordPosition(EntityPlayer player)
    {
        //Update position
        playerBlockPos = new BlockPos(
                (int) Math.floor(player.posX),
                (int) Math.floor(player.posY),
                (int) Math.floor(player.posZ));
    }

    public boolean checkCanSeeSky()
    {
        return playerWorld.dimension.hasSkyLight() && (playerWorld.canBlockSeeSky(playerBlockPos.up()) || doRayCheckSky());
    }

    //Does a basic check to see if there is a solid block above us
    private boolean doRayCheckSky()
    {
        BlockPos blockPos = playerBlockPos.up();
        do {
            IBlockState state = playerWorld.getBlockState(blockPos);
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