package io.github.strikerrocker.magicmirror;

import io.github.strikerrocker.magicmirror.capability.IMirrorData;
import io.github.strikerrocker.magicmirror.config.Config;
import io.github.strikerrocker.magicmirror.mirror.MirrorItem;
import io.github.strikerrocker.magicmirror.mirror.MirrorSubType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public MagicMirror() {
        Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("magic_mirror.toml"));
    }

    @SubscribeEvent
    public static void capabilityRegisterEvent(RegisterCapabilitiesEvent event) {
        event.register(IMirrorData.class);
    }

    @SubscribeEvent
    public static void registerItem(RegisterEvent registry) {
        if (registry.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
            for (MirrorSubType type : MirrorSubType.values()) {
                registry.register(ForgeRegistries.Keys.ITEMS,
                        new ResourceLocation(DOMAIN, "magicmirror_" + type.toString().toLowerCase()),
                        () -> new MirrorItem(type));
            }
        }
    }
}