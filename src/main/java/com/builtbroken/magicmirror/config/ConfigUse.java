package com.builtbroken.magicmirror.config;


import net.minecraftforge.common.ForgeConfigSpec;

import static com.builtbroken.magicmirror.MagicMirror.DOMAIN;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
public class ConfigUse
{
    /**
     * Amount of time to delay before resetting sky lose count
     */
    public static ForgeConfigSpec.IntValue SKY_COOLDOWN;

    /**
     * Amount of time to delay before resetting onSurface count
     */
    public static ForgeConfigSpec.IntValue SURFACE_COOLDOWN;

    /**
     * Amount of time to delay before setting teleport location
     */
    public static ForgeConfigSpec.IntValue TP_SET_DELAY;

    /**
     * Minimal amount of time the user has to be on the surface for the mirror to record a location
     */
    public static ForgeConfigSpec.IntValue MIN_SURFACE_TIME;

    /**
     * Distance in X & Z that player can travel and still teleport
     */
    public static ForgeConfigSpec.IntValue TELEPORT_BREAK_DISTANCE;

    public static ForgeConfigSpec.IntValue TICKS_BEFORE_TELEPORT; //5 seconds

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER)
    {
        SERVER_BUILDER.comment("Use");

        SKY_COOLDOWN = SERVER_BUILDER.comment("Time delay in ticks (20 ticks a second) before counting as not under the sky")
                .defineInRange(DOMAIN + ".sky_cooldown", 5 * 20, 5, 1000000000);
        SURFACE_COOLDOWN = SERVER_BUILDER.comment("Time delay in ticks (20 ticks a second) before counting as on the surface")
                .defineInRange(DOMAIN + ".surface_cooldown", 5 * 20, 5, 1000000000);
        TP_SET_DELAY = SERVER_BUILDER.comment("Time delay in ticks (20 ticks a second) before creating a new teleport location")
                .defineInRange(DOMAIN + ".position_set_cooldown", 5 * 20, 5, 1000000000);
        MIN_SURFACE_TIME = SERVER_BUILDER.comment("Time delay in ticks (20 ticks a second) the player needs to be on the surface to record a location. " +
                "This acts as a charging mechanic for the mirror. Higher amount of time means longer the mirror needs to charge" +
                "in sun light")
                .defineInRange(DOMAIN + ".min_surface_time", 60 * 20, 5, 1000000000);
        TELEPORT_BREAK_DISTANCE = SERVER_BUILDER.comment("Distance in meters (block) ,ignoring height, for teleport position to be cleared. Set to -1 to allow unlimited range.")
                .defineInRange(DOMAIN + ".teleport_break_distance", 200, -1, 1000000000);
        TICKS_BEFORE_TELEPORT = SERVER_BUILDER.comment("Time in ticks (20 ticks a second) to hold the mirror in order to teleport")
                .defineInRange(DOMAIN + ".teleport_hold_delay", 5 * 20, 5, 1000000000);
    }
}
