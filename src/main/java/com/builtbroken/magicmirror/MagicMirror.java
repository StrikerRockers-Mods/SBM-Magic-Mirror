package com.builtbroken.magicmirror;

import com.builtbroken.magicmirror.capability.IMirrorData;
import com.builtbroken.magicmirror.capability.MirrorData;
import com.builtbroken.magicmirror.capability.MirrorStorage;
import com.builtbroken.magicmirror.config.Config;
import com.builtbroken.magicmirror.mirror.ItemMirror;
import com.builtbroken.magicmirror.mirror.MirrorSubType;
import net.minecraft.item.Item;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Very simple mod to help users get back to the start of a dungeon after clear it. The mod works by tracking the user's position and recording the last
 * position the user was before going underground. Then when users the mirror will teleport the user to the last recorded position.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(value = MagicMirror.DOMAIN)
public class MagicMirror
{
    //TODO make the mirror glow when it can be used
    //TODO make it have a visual change when it has a stored location
    //TODO show some kind of progress bar for XP needed
    //TODO Event to fire(Marks position, etc)

    /**
     * Are we running in developer mode, used to enabled additional debug and tools
     */
    public static final boolean runningAsDev = System.getProperty("development") != null && System.getProperty("development").equalsIgnoreCase("true");
    /**
     * Information output thing
     */
    public static final Logger logger = LogManager.getLogger("SBM-MagicMirror");
    public static final String DOMAIN = "sbmmagicmirror";
    public static List<ItemMirror> mirrors = new ArrayList<>();
    /**
     * Mirror item used to activate and tick mirror handler
     */
    public static ItemMirror silverMirror;

    public MagicMirror()
    {
        Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("magic_mirror.toml"));
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> registry)
    {
        for (MirrorSubType type : MirrorSubType.values()) {
            ItemMirror item = new ItemMirror(type);
            mirrors.add(item);
            registry.getRegistry().register(item);
            if (type == MirrorSubType.SILVER_DIRTY) {
                silverMirror = item;
            }
        }
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event)
    {
        //Capability
        CapabilityManager.INSTANCE.register(IMirrorData.class, new MirrorStorage(), () -> new MirrorData());
    }
}