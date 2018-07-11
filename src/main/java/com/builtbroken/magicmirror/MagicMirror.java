package com.builtbroken.magicmirror;

import com.builtbroken.magicmirror.capability.IMirrorData;
import com.builtbroken.magicmirror.capability.MirrorData;
import com.builtbroken.magicmirror.capability.MirrorStorage;
import com.builtbroken.magicmirror.config.ConfigLoot;
import com.builtbroken.magicmirror.mirror.ItemMirror;
import com.builtbroken.magicmirror.network.PacketClientUpdate;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
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

    @Mod.Instance(DOMAIN)
    public static MagicMirror INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.magicmirror.ClientProxy", serverSide = "com.builtbroken.magicmirror.CommonProxy")
    public static CommonProxy proxy;

    /**
     * Mirror item used to activate and tick mirror handler
     */
    public static ItemMirror itemMirror;

    public static SimpleNetworkWrapper network;

    //@SubscribeEvent
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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(IMirrorData.class, new MirrorStorage(), MirrorData.class);

        network = NetworkRegistry.INSTANCE.newSimpleChannel(DOMAIN);
        network.registerMessage(PacketClientUpdate.Handler.class, PacketClientUpdate.class, 1, Side.CLIENT);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        //TODO add admin command to clear location of mirror
        //TODO add admin command to activate mirror
        //TODO add admin command to  set location
    }
}