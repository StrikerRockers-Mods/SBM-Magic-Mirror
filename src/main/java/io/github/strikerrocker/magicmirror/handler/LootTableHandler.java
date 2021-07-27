package io.github.strikerrocker.magicmirror.handler;

import io.github.strikerrocker.magicmirror.MagicMirror;
import io.github.strikerrocker.magicmirror.config.ConfigLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN)
public class LootTableHandler {
    ///setblock ~ ~ ~ minecraft:chest 0 replace {LootTable:"minecraft:chests/simple_dungeon"}

    @SubscribeEvent
    public static void registerLoot(LootTableLoadEvent evt) {
        String prefix = "minecraft:chests/";
        String name = evt.getName().toString();
        if (name.startsWith(prefix) && ConfigLoot.enableAsDungeonLoot.get()) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            switch (file) {
                case "abandoned_mineshaft":
                case "desert_pyramid":
                case "simple_dungeon":
                case "stronghold_library":
                case "village/village_toolsmith":
                case "end_city_treasure":
                case "nether_bridge":
                case "jungle_temple":
                case "woodland_mansion":
                case "stronghold_corridor":
                case "stronghold_crossing":
                    evt.getTable().addPool(getInjectPool(file));
                    break;
                default:
                    break;
            }
        }
    }

    public static LootPool getInjectPool(String entryName) {
        return LootPool.lootPool()
                .add(getInjectEntry(entryName))
                .bonusRolls(0, 1)
                .name("inject")
                .build();
    }

    private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name) {
        ResourceLocation table = new ResourceLocation(MagicMirror.DOMAIN, "inject/" + name);
        return LootTableReference.lootTableReference(table)
                .setWeight(1);
    }
}
