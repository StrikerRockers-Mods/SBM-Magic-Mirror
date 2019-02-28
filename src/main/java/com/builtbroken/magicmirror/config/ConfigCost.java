package com.builtbroken.magicmirror.config;

import com.builtbroken.magicmirror.MagicMirror;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
//@Config(modid = MagicMirror.DOMAIN, name = "sbm/magic_mirror/cost")
//@Config.LangKey("config.sbmmagicmirror:cost.title")
public class ConfigCost
{
    /**
     * Should we use XP when teleporting with the mirror
     */
    public static ForgeConfigSpec.BooleanValue USE_XP;

    /**
     * Is the xp cost a flat rate, true will consume xp equal to {@link #XP_COST}, false will use it as a multiplier per meter traveled
     */
    public static ForgeConfigSpec.BooleanValue FLAT_RATE;
    /**
     * Amount of XP consumed, @see {@link #FLAT_RATE} for additional details
     */
    public static ForgeConfigSpec.IntValue XP_COST;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER)
    {
        SERVER_BUILDER.comment("Costs");

        USE_XP = SERVER_BUILDER.comment("Should using the mirror require xp").define(MagicMirror.DOMAIN + ".use_xp", true);

        FLAT_RATE = SERVER_BUILDER.comment("Should xp be used as a flat rate (true) or as a cost per meter traveled (false)")
                .define(MagicMirror.DOMAIN + ".flat_xp_use", false);

        XP_COST = SERVER_BUILDER.comment("Cost in xp to use the mirror").defineInRange(MagicMirror.DOMAIN + ".xp_cost", 1, 0, 10000000);
    }
}