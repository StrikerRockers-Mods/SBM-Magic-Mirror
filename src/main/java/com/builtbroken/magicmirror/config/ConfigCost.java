package com.builtbroken.magicmirror.config;

import com.builtbroken.magicmirror.MagicMirror;
import net.minecraftforge.common.config.Config;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
@Config(modid = MagicMirror.DOMAIN, name = "sbm/magic_mirror/cost")
@Config.LangKey("config.sbmmagicmirror:cost.title")
public class ConfigCost
{
    /**
     * Should we use XP when teleporting with the mirror
     */
    @Config.Name("use_xp")
    @Config.Comment("Should using the mirror require xp")
    public static boolean USE_XP = true;
    /**
     * Is the xp cost a flat rate, true will consume xp equal to {@link #XP_COST}, false will use it as a multiplier per meter traveled
     */
    @Config.Name("flat_xp_use")
    @Config.Comment("Should xp be used as a flat rate (true) or as a cost per meter traveled (false)")
    public static boolean FLAT_RATE = false;
    /**
     * Amount of XP consumed, @see {@link #FLAT_RATE} for additional details
     */
    @Config.Name("xp_cost")
    @Config.Comment("Cost in xp to use the mirror")
    public static float XP_COST = 1;
}
