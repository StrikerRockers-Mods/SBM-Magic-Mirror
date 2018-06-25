package com.builtbroken.magicmirror.handler;

import com.builtbroken.magicmirror.MagicMirror;
import net.minecraftforge.common.config.Config;

/**
 * Created by StrikerRocker on 21/6/18.
 */
@Config(modid = MagicMirror.DOMAIN, name = "bbm/Magic_Mirror")
public class ConfigMain
{
    @Config.Comment("Sets the max distance for the mirror to continue to save and allow teleportion")
    @Config.RangeInt(min = -1, max = 999999999)
    @Config.Name("Max Teleport Distance")
    public static int Max_Teleport_Distance = 200;

    @Config.Comment("How long in ticks the user has to wait before teleporting")
    @Config.RangeInt(min = -1, max = 1200)
    @Config.Name("Activation Time")
    public static int Activation_Time = 200;

    @Config.Comment("Should mirrors be found inside of dungeons as loot")
    public static boolean EnableAsDungeonLoot = true;
}
