package com.builtbroken.magicmirror;

import com.builtbroken.magicmirror.capability.IMirrorData;
import com.builtbroken.magicmirror.capability.MirrorData;
import com.builtbroken.magicmirror.capability.MirrorStorage;
import com.builtbroken.magicmirror.mirror.ItemMirror;
import com.builtbroken.magicmirror.network.PacketClientUpdate;
import net.minecraft.item.Item;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Very simple mod to help users get back to the start of a dungeon after clear it. The mod works by tracking the user's position and recording the last
 * position the user was before going underground. Then when users the mirror will teleport the user to the last recorded position.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
@Mod.EventBusSubscriber
@Mod(modid = MagicMirror.DOMAIN, name = "SMB Magic Mirror", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
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

    /**
     * Mirror item used to activate and tick mirror handler
     */
    public static ItemMirror itemMirror;

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Capability
        CapabilityManager.INSTANCE.register(IMirrorData.class, new MirrorStorage(), () -> new MirrorData(null));

        //Network
        network = NetworkRegistry.INSTANCE.newSimpleChannel(DOMAIN);
        network.registerMessage(PacketClientUpdate.Handler.class, PacketClientUpdate.class, 1, Side.CLIENT);
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> registry)
    {
        registry.getRegistry().register(itemMirror = new ItemMirror());
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (DOMAIN.equals(event.getModID()))
        {
            ConfigManager.sync(DOMAIN, Config.Type.INSTANCE);
        }
    }
}