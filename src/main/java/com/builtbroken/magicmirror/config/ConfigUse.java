package com.builtbroken.magicmirror.config;

import com.builtbroken.magicmirror.MagicMirror;
import net.minecraftforge.common.config.Config;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
@Config(modid = MagicMirror.DOMAIN, name = "sbm/magic_mirror/use")
@Config.LangKey("config.sbmmagicmirror:use.title")
public class ConfigUse
{
    /**
     * Amount of time to delay before resetting sky lose count
     */
    @Config.Name("sky_cooldown")
    @Config.Comment("Time delay in ticks (20 ticks a second) before counting as not under the sky")
    @Config.RangeInt(min = 5)
    public static int SKY_COOLDOWN = 5 * 20; //5 seconds

    /**
     * Amount of time to delay before resetting onSurface count
     */
    @Config.Name("surface_cooldown")
    @Config.Comment("Time delay in ticks (20 ticks a second) before counting as on the surface")
    @Config.RangeInt(min = 5)
    public static int SURFACE_COOLDOWN = 5 * 20; //5 seconds

    /**
     * Amount of time to delay before setting teleport location
     */
    @Config.Name("position_set_cooldown")
    @Config.Comment("Time delay in ticks (20 ticks a second) before creating a new teleport location")
    @Config.RangeInt(min = 5)
    public static int TP_SET_DELAY = 5 * 20; //5 seconds

    /**
     * Minimal amount of time the user has to be on the surface for the mirror to record a location
     */
    @Config.Name("min_surface_time")
    @Config.Comment("Time delay in ticks (20 ticks a second) the player needs to be on the surface to record a location. " +
            "This acts as a charging mechanic for the mirror. Higher amount of time means longer the mirror needs to charge" +
            "in sun light")
    @Config.RangeInt(min = 5)
    public static int MIN_SURFACE_TIME = 60 * 20; //1 min

    /**
     * Distance in X & Z that player can travel and still teleport
     */
    @Config.Name("max_teleport_distance")
    @Config.Comment("Distance in meters (block) to allow a player to teleport to from current position.")
    @Config.RangeInt(min = 5)
    public static int MAX_TELEPORT_DISTANCE = 200;

    @Config.Name("teleport_hold_delay")
    @Config.Comment("Time in ticks (20 ticks a second) to hold the mirror in order to teleport")
    @Config.RangeInt(min = 5)
    public static int TICKS_BEFORE_TELEPORT = 5 * 20; //5 seconds
}
