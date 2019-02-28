package com.builtbroken.magicmirror.config;


import com.builtbroken.magicmirror.MagicMirror;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
public class ConfigLoot
{
    public static ForgeConfigSpec.BooleanValue enableAsDungeonLoot;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER)
    {
        SERVER_BUILDER.comment("Loot");

        enableAsDungeonLoot = SERVER_BUILDER.comment("Set to true to allow loot to spawn in vanilla minecraft dungeons")
                .define(MagicMirror.DOMAIN + ".enable_dungeon_loot", true);
    }
}
