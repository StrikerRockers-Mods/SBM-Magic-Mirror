package io.github.strikerrocker.magicmirror;

import io.github.strikerrocker.magicmirror.capability.IMirrorData;
import io.github.strikerrocker.magicmirror.config.Config;
import io.github.strikerrocker.magicmirror.mirror.MirrorItem;
import io.github.strikerrocker.magicmirror.mirror.MirrorSubType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(value = MagicMirror.DOMAIN)
public class MagicMirror {
    public static final Logger logger = LogManager.getLogger("SBM-MagicMirror");
    public static final String DOMAIN = "sbmmagicmirror";
    public static List<MirrorItem> mirrors = new ArrayList<>();
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
    public static void registerItem(RegistryEvent.Register<Item> registry) {
        for (MirrorSubType type : MirrorSubType.values()) {
            MirrorItem item = new MirrorItem(type);
            mirrors.add(item);
            registry.getRegistry().register(item);
        }
    }
}