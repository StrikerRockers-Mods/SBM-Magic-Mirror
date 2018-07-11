package com.builtbroken.magicmirror.config;

import com.builtbroken.magicmirror.MagicMirror;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN)
public class ConfigLoot
{
    @Config.Comment("Set to true to allow loot to spawn in vanilla minecraft dungeons")
    @Config.Name("enable_dungeon_loot")
    public static boolean EnableAsDungeonLoot = true;


    @SubscribeEvent
    public static void registerLoot(LootTableLoadEvent event)
    {
        final String VANILLA_LOOT_POOL_ID = "main";
        LootPool lootPool = event.getTable().getPool(VANILLA_LOOT_POOL_ID);
        if (lootPool != null)
        {
            if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT))
            {
                if (ConfigLoot.EnableAsDungeonLoot)
                {
                    //TODO monster drops (super rare, .0003) and chest drop(common, 0.15)
                    //TODO make diamond and gold mirror super rare
                    //WeightedRandomChestContent silverMirror = new WeightedRandomChestContent(itemMirror, 3, 0, 1, 5); //TODO add config to tweek drop rate
                    //WeightedRandomChestContent goldMirror = new WeightedRandomChestContent(itemMirror, 4, 0, 1, 2); //TODO add config to tweek drop rate
                    //WeightedRandomChestContent diamondMirror = new WeightedRandomChestContent(itemMirror, 5, 0, 1, 1); //TODO add config to tweek drop rate

                    //ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(silverMirror);
                    //ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(silverMirror);
                    //ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(silverMirror);
                    //ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(silverMirror);
                    //ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(silverMirror);
                    //ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CROSSING).addItem(silverMirror);
                    //ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(silverMirror);

                    //ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(goldMirror);
                    //ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(diamondMirror);
                }
            }
        }
    }
}
