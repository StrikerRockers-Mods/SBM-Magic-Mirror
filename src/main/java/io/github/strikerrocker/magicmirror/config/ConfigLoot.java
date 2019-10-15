package io.github.strikerrocker.magicmirror.config;


import io.github.strikerrocker.magicmirror.MagicMirror;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigLoot {
    public static ForgeConfigSpec.BooleanValue enableAsDungeonLoot;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Loot");

        enableAsDungeonLoot = SERVER_BUILDER.comment("Set to true to allow loot to spawn in vanilla minecraft dungeons")
                .define(MagicMirror.DOMAIN + ".enable_dungeon_loot", true);
    }
}
