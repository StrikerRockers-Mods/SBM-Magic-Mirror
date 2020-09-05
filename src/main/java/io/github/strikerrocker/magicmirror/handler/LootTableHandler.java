package io.github.strikerrocker.magicmirror.handler;

import io.github.strikerrocker.magicmirror.MagicMirror;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN)
public class LootTableHandler {
    ///setblock ~ ~ ~ minecraft:chest 0 replace {LootTable:"minecraft:chests/simple_dungeon"}
    //TODO add configs for drop rates and what loot tables to support
    //private static final String VANILLA_LOOT_POOL_ID = "main";
    //private static HashMap<ResourceLocation, LootEntry[]> lootEntries = new HashMap();

    /*static {
        //Normal loot
        lootEntries.put(LootTables.CHESTS_END_CITY_TREASURE,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                }
        );
        lootEntries.put(LootTables.CHESTS_SIMPLE_DUNGEON,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTables.CHESTS_VILLAGE_VILLAGE_TOOLSMITH,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTables.CHESTS_ABANDONED_MINESHAFT,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTables.CHESTS_NETHER_BRIDGE,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTables.CHESTS_STRONGHOLD_LIBRARY,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });
        lootEntries.put(LootTables.CHESTS_DESERT_PYRAMID,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 5, 1),
                        newEntry(MirrorSubType.SILVER, 2, 2)
                });

        //Rare loot
        lootEntries.put(LootTables.CHESTS_JUNGLE_TEMPLE,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 15, 1),
                        newEntry(MirrorSubType.SILVER, 12, 2),
                        newEntry(MirrorSubType.GOLD_DIRTY, 8, 1),
                        newEntry(MirrorSubType.GOLD, 3, 2),
                        newEntry(MirrorSubType.DIAMOND_DIRTY, 1, 1),
                        newEntry(MirrorSubType.DIAMOND, 0, 2)
                });
        lootEntries.put(LootTables.CHESTS_WOODLAND_MANSION,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 15, 1),
                        newEntry(MirrorSubType.SILVER, 12, 2),
                        newEntry(MirrorSubType.GOLD_DIRTY, 8, 1),
                        newEntry(MirrorSubType.GOLD, 3, 2),
                        newEntry(MirrorSubType.DIAMOND_DIRTY, 1, 1),
                        newEntry(MirrorSubType.DIAMOND, 0, 2)
                });

        lootEntries.put(LootTables.CHESTS_STRONGHOLD_CROSSING,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 15, 1),
                        newEntry(MirrorSubType.SILVER, 12, 2),
                        newEntry(MirrorSubType.GOLD_DIRTY, 8, 1),
                        newEntry(MirrorSubType.GOLD, 3, 2),
                        newEntry(MirrorSubType.DIAMOND_DIRTY, 1, 1),
                        newEntry(MirrorSubType.DIAMOND, 0, 2)
                });
        lootEntries.put(LootTables.CHESTS_STRONGHOLD_CORRIDOR,
                new LootEntry[]{
                        newEntry(MirrorSubType.SILVER_DIRTY, 15, 1),
                        newEntry(MirrorSubType.SILVER, 12, 2),
                        newEntry(MirrorSubType.GOLD_DIRTY, 8, 1),
                        newEntry(MirrorSubType.GOLD, 3, 2),
                        newEntry(MirrorSubType.DIAMOND_DIRTY, 1, 1),
                        newEntry(MirrorSubType.DIAMOND, 0, 2)
                });
    }

    private static final LootEntry newEntry(MirrorSubType type, int weight, int quality) {
        return new LootEntryItemStack(MagicMirror.DOMAIN + ":mirror." + type.name().toLowerCase().replace("_", "."),
                new ItemStack(MagicMirror.silverMirror, 1),
                weight, quality
        );
    }


    @SubscribeEvent
    public static void registerLoot(LootTableLoadEvent event) {
        if (ConfigLoot.enableAsDungeonLoot.get()) {
            if (lootEntries.containsKey(event.getName())) {
                LootPool lootPool = event.getTable().getPool(VANILLA_LOOT_POOL_ID);
                if (lootPool != null) {
                    LootEntry[] entries = lootEntries.get(event.getName());
                    for (LootEntry entry : entries) {
                        //TODO lootPool.addEntry(entry);
                    }
                }
            }
        }
    }*/
}
