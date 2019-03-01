package com.builtbroken.magicmirror.config;

import com.builtbroken.magicmirror.MagicMirror;
import net.minecraftforge.common.config.Config;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
@Config(modid = MagicMirror.DOMAIN, name = "sbm/magic_mirror/loot")
public class ConfigLoot
{
    @Config.Comment("Set to true to allow loot to spawn in vanilla minecraft dungeons")
    @Config.Name("enable_dungeon_loot")
    public static boolean enableAsDungeonLoot = true;
}
