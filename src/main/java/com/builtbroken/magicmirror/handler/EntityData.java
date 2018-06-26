package com.builtbroken.magicmirror.handler;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.capability.IMirrorData;
import com.builtbroken.magicmirror.network.PacketClientUpdate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import static com.builtbroken.magicmirror.capability.MirrorStorage.CAPABILITY_MIRROR;

/**
 * Stores data about the entity in order to track movement, time on surface, etc....
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/9/2016.
 */
public class EntityData
{
    //TODO make per use configurable threw commands [within reason]
    /**
     * Amount of time to delay before resetting sky lose count
     */
    public static final int SKY_COOLDOWN = 5 * 20; //5 seconds
    /**
     * Amount of time to delay before resetting onSurface count
     */
    public static final int SURFACE_COOLDOWN = 5 * 20; //5 seconds
    /**
     * Amount of time to delay before setting teleport location
     */
    public static final int TP_SET_DELAY = 5 * 20; //5 seconds
    /**
     * Minimal amount of time the user has to be on the surface for the mirror to record a location
     */
    public static final int MIN_SURFACE_TIME = 60 * 20; //1 min
    /**
     * Distance in X & Z that player can travel and still teleport
     */
    public static int MAX_TELEPORT_DISTANCE = 200;


    //Last tick location data
    public World world;
    public int x;
    public int y;
    public int z;

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
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static IMirrorData getHandler(EntityPlayer entity)
    {

        if (entity.hasCapability(CAPABILITY_MIRROR, EnumFacing.DOWN))
            return entity.getCapability(CAPABILITY_MIRROR, EnumFacing.DOWN);
        return null;
    }

    //Clears data, normally called when TP location is set
    private void reset()
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
        return y >= world.provider.getHorizon();
    }

    /**
     * Called to update the position data for the entity
     *
     * @param player - entity to user coords for
     */
    public void update(EntityPlayer player)
    {
        //Ensure we only update once a tick, Patch to fix if user has several mirrors in inventory
        if (lastTickTime == 0 || (System.currentTimeMillis() - lastTickTime) >= 50)
        {
            tick++;
            if (tick + 1 >= Integer.MAX_VALUE)
            {
                tick = 0;
            }
            try
            {
                //Update position
                this.x = (int) player.posX;
                this.y = (int) player.posY;
                this.z = (int) player.posZ;

                if (MAX_TELEPORT_DISTANCE > -1 && getHandler(player).hasLocation())
                {
                    TeleportPos pos = getHandler(player).getLocation();
                    double distance = Math.sqrt(Math.pow(pos.x - x, 2) + Math.pow(pos.y - y, 2) + Math.pow(pos.z - z, 2));
                    if (distance >= MAX_TELEPORT_DISTANCE)
                    {
                        //TODO add config to disable this action
                        player.getCapability(CAPABILITY_MIRROR, EnumFacing.DOWN).setLocation(null);
                        player.sendStatusMessage(new TextComponentTranslation("item.sbmmagicmirror:magicmirror.error.link.broken.distance"), true);
                    }
                }

                //Get current data from new position
                final boolean isOnSurface = checkOnSurface();
                final boolean canSeeSky = checkCanSeeSky();

                //were we on the surface last tick and this tick
                final boolean stillOnSurface = this.wasOnSurface && isOnSurface && couldSeeSky && canSeeSky;

                //TODO reset location as soon as user is considered above ground [Timer]

                //Surface last tick and this tick -> increase time recorded
                if (stillOnSurface)
                {
                    timeAboveGround++;
                    if (getHandler(player).hasLocation() && timeAboveGround >= SURFACE_COOLDOWN)
                    {
                        getHandler(player).setLocation(player, null);
                    }
                    if (timeAboveGround == MIN_SURFACE_TIME)
                    {
                        player.sendStatusMessage(new TextComponentString("Mirror charged by the love of the sky"), true);
                        //TODO Randomize message
                        //TODO add command to enable/disable message
                    }
                }
                //if min time -> can't see sky -> no tp loc -> set loc
                else if (timeAboveGround >= MIN_SURFACE_TIME)
                {
                    if (!canSeeSky && potentialTP == null)
                    {
                        potentialTP = new TeleportPos(player);
                        if (getHandler(player).hasLocation())
                        {
                            getHandler(player).setLocation(player, null);
                        }
                    }
                }

                //No sky -> record tp location and tick until we can set it
                if (!canSeeSky)
                {
                    timeWithoutSky++;
                    if (potentialTP != null && timeWithoutSky >= TP_SET_DELAY)
                    {
                        getHandler(player).setLocation(player, potentialTP);
                        reset();
                    }
                    if (timeOnSurfaceCooldown++ >= SURFACE_COOLDOWN)
                    {
                        timeAboveGround = 0;
                        timeOnSurfaceCooldown = 0;
                    }
                } else if (timeWithoutSkyCooldown++ >= SKY_COOLDOWN)
                {
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
                //"Mirror feels longely"
                // User holds it "Mirror feels loved"
                // User hovers over it "Mirror wants to be held"

                //If user has a lot of mirrors
                //"We want to be few"
                //User holds it "I feel special"
                //User hovers over it "There can only be one"


                //Send packet update to usernew BlockPos(x,y,z)
                if (player instanceof EntityPlayerMP && (tick == 1 || tick % 5 == 0))
                {
                    MagicMirror.network.sendTo(new PacketClientUpdate((int) getHandler(player).getXpTeleportCost(player), timeAboveGround >= MIN_SURFACE_TIME, getHandler(player).hasLocation()), (EntityPlayerMP) player);

                }
            } catch (Exception e)
            {
                if (MagicMirror.runningAsDev)
                {
                    MagicMirror.logger.error("EntityData failed to update information about entity", e);
                } else
                {
                    MagicMirror.logger.error("EntityData failed to update information about entity, errored with message: [ " + e.getMessage() + " ] enable dev mod for more detailed info.");
                }
            }
            lastTickTime = System.currentTimeMillis();
        }
    }

    public boolean checkCanSeeSky()
    {
        return !world.provider.hasSkyLight() && (world.canBlockSeeSky(new BlockPos(x,y,z) ) || doRayCheckSky());
    }

    //Does a basic check to see if there is a solid block above us
    private boolean doRayCheckSky()
    {
        for (int y = this.y; y <= world.getActualHeight(); y++)
        {
            //TODO check if block is full
            IBlockState state = world.getBlockState(new BlockPos(x,y,z));
            Block block = state.getBlock();
            if (block.getMaterial(state) != Material.AIR && block.isOpaqueCube(state))
            {
                return false;
            }
        }
        return true;
    }
}
