package io.github.strikerrocker.magicmirror;

import io.github.strikerrocker.magicmirror.capability.IMirrorData;
import io.github.strikerrocker.magicmirror.config.Config;
import io.github.strikerrocker.magicmirror.mirror.MirrorItem;
import io.github.strikerrocker.magicmirror.mirror.MirrorSubType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(value = MagicMirror.DOMAIN)
public class MagicMirror {
    public static final Logger logger = LogManager.getLogger("SBM-MagicMirror");
    public static final String DOMAIN = "sbmmagicmirror";
    /**
     * Mirror item used to activate and tick mirror handler
     */
    public static Capability<IMirrorData> CAPABILITY_MIRROR = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static List<Item> MIRRORS = new ArrayList<>();

    public MagicMirror() {
        Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("magic_mirror.toml"));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(MagicMirror::itemGroup);
    }

    @SubscribeEvent
    public static void capabilityRegisterEvent(RegisterCapabilitiesEvent event) {
        event.register(IMirrorData.class);
    }

    @SubscribeEvent
    public static void registerItem(RegisterEvent registry) {
        if (registry.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
            for (MirrorSubType type : MirrorSubType.values()) {
                Item mirror = new MirrorItem(type);
                registry.register(ForgeRegistries.Keys.ITEMS,
                        new ResourceLocation(DOMAIN, "magicmirror_" + type.toString().toLowerCase()),
                        () -> mirror);
                MIRRORS.add(mirror);
            }
        }
    }

    public static void itemGroup(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) MIRRORS.forEach(event::accept);
    }

}