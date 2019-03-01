package com.builtbroken.magicmirror.handler;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.config.ConfigLoot;
import com.builtbroken.magicmirror.mirror.MirrorSubType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/11/2018.
 */
@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN)
public class LootTableHandler
{
    ///setblock ~ ~ ~ minecraft:chest 0 replace {LootTable:"minecraft:chests/simple_dungeon"}
    //TODO add configs for drop rates and what loot tables to support
    private static final String VANILLA_LOOT_POOL_ID = "main";
    private static HashMap<ResourceLocation, LootEntry[]> lootEntries = new HashMap();

    static
    {
        //Normal loot
        lootEntries.put(LootTableList.CHESTS_END_CITY_TREASURE,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                }
        );
        lootEntries.put(LootTableList.CHESTS_SIMPLE_DUNGEON,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTableList.CHESTS_VILLAGE_BLACKSMITH,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTableList.CHESTS_ABANDONED_MINESHAFT,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTableList.CHESTS_NETHER_BRIDGE,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTableList.CHESTS_STRONGHOLD_LIBRARY,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTableList.CHESTS_DESERT_PYRAMID,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });

        //Rare loot
        lootEntries.put(LootTableList.CHESTS_JUNGLE_TEMPLE,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 15, 1),
                        newEntry(MirrorSubType.SILVER, 12, 2),
                        newEntry(MirrorSubType.GOLD_DIRTY, 8, 1),
                        newEntry(MirrorSubType.GOLD, 3, 2),
                        newEntry(MirrorSubType.DIAMOND_DIRTY, 1, 1),
                        newEntry(MirrorSubType.DIAMOND, 0, 2)
                });
        lootEntries.put(LootTableList.CHESTS_WOODLAND_MANSION,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 15, 1),
                        newEntry(MirrorSubType.SILVER, 12, 2),
                        newEntry(MirrorSubType.GOLD_DIRTY, 8, 1),
                        newEntry(MirrorSubType.GOLD, 3, 2),
                        newEntry(MirrorSubType.DIAMOND_DIRTY, 1, 1),
                        newEntry(MirrorSubType.DIAMOND, 0, 2)
                });

        lootEntries.put(LootTableList.CHESTS_STRONGHOLD_CROSSING,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 15, 1),
                        newEntry(MirrorSubType.SILVER, 12, 2),
                        newEntry(MirrorSubType.GOLD_DIRTY, 8, 1),
                        newEntry(MirrorSubType.GOLD, 3, 2),
                        newEntry(MirrorSubType.DIAMOND_DIRTY, 1, 1),
                        newEntry(MirrorSubType.DIAMOND, 0, 2)
                });
        lootEntries.put(LootTableList.CHESTS_STRONGHOLD_CORRIDOR,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 15, 1),
                        newEntry(MirrorSubType.SILVER, 12, 2),
                        newEntry(MirrorSubType.GOLD_DIRTY, 8, 1),
                        newEntry(MirrorSubType.GOLD, 3, 2),
                        newEntry(MirrorSubType.DIAMOND_DIRTY, 1, 1),
                        newEntry(MirrorSubType.DIAMOND, 0, 2)
                });
    }

    private static final LootEntry newEntry(MirrorSubType type, int weight, int quality)
    {
        return new LootEntryItemStack(MagicMirror.DOMAIN + ":mirror." + type.name().toLowerCase().replace("_", "."),
                new ItemStack(MagicMirror.itemMirror, 1, MirrorSubType.SILVER_DIRTY.ordinal()),
                weight, quality
        );
    }


    @SubscribeEvent
    public static void registerLoot(LootTableLoadEvent event)
    {
        if (ConfigLoot.enableAsDungeonLoot)
        {
            if (lootEntries.containsKey(event.getName()))
            {
                LootPool lootPool = event.getTable().getPool(VANILLA_LOOT_POOL_ID);
                if (lootPool != null)
                {
                    LootEntry[] entries = lootEntries.get(event.getName());
                    for (LootEntry entry : entries)
                    {
                        lootPool.addEntry(entry);
                    }
                }
            }
        }
    }
}
