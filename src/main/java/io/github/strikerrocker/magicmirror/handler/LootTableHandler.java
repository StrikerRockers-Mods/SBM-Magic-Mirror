package io.github.strikerrocker.magicmirror.handler;

import io.github.strikerrocker.magicmirror.MagicMirror;
import io.github.strikerrocker.magicmirror.config.ConfigLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
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
                case "abandoned_mineshaft", "desert_pyramid", "simple_dungeon", "stronghold_library", "village/village_toolsmith", "end_city_treasure", "nether_bridge", "jungle_temple", "woodland_mansion", "stronghold_corridor", "stronghold_crossing" -> evt.getTable().addPool(getInjectPool(file));
                default -> {
                }
            }
        }
    }

    public static LootPool getInjectPool(String entryName) {
        return LootPool.lootPool()
                .add(getInjectEntry(entryName))
                .setBonusRolls(UniformGenerator.between(0, 1))
                .name("inject")
                .build();
    }

    private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name) {
        ResourceLocation table = new ResourceLocation(MagicMirror.DOMAIN, "inject/" + name);
        return LootTableReference.lootTableReference(table)
                .setWeight(1);
    }
}
