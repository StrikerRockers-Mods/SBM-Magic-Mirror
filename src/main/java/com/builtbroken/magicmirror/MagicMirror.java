package com.builtbroken.magicmirror;

import com.builtbroken.magicmirror.handler.ConfigHandler;
import com.builtbroken.magicmirror.handler.EntityData;
import com.builtbroken.magicmirror.handler.capability.IMirrorData;
import com.builtbroken.magicmirror.handler.capability.MirrorData;
import com.builtbroken.magicmirror.handler.capability.MirrorStorage;
import com.builtbroken.magicmirror.mirror.ItemMirror;
import com.builtbroken.magicmirror.mirror.ModelMirror;
import com.builtbroken.magicmirror.network.PacketClientUpdate;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
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

    /** Are we running in developer mode, used to enabled additional debug and tools */
    public static final boolean runningAsDev = System.getProperty("development") != null && System.getProperty("development").equalsIgnoreCase("true");

    /** Information output thing */
    public static final Logger logger = LogManager.getLogger("SBM-MagicMirror");

    public static final String DOMAIN = "smbmagicmirror";

    /** Should we use XP when teleporting with the mirror */
    public static boolean USE_XP = true;
    /** Is the xp cost a flat rate, true will consume xp equal to {@link #XP_COST}, false will use it as a multiplier per meter traveled */
    public static boolean FLAT_RATE = false;
    /** Amount of XP consumed, @see {@link #FLAT_RATE} for additional details */
    public static float XP_COST = 1;

    @Mod.Instance(DOMAIN)
    public static MagicMirror INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.magicmirror.ClientProxy", serverSide = "com.builtbroken.magicmirror.CommonProxy")
    public static CommonProxy proxy;

    /** Mirror item used to activate and tick mirror handler */
    public static ItemMirror itemMirror;

    public static SimpleNetworkWrapper network;

    @SubscribeEvent
    public static void registerLoot(LootTableLoadEvent event)
    {
        final String VANILLA_LOOT_POOL_ID = "main";
        LootPool lootPool = event.getTable().getPool(VANILLA_LOOT_POOL_ID);
        if (lootPool != null)
        {
            if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT))
            {
                if (ConfigHandler.EnableAsDungeonLoot)
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

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> registry)
    {
        registry.getRegistry().register(itemMirror);
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (DOMAIN.equals(event.getModID()))
        {
            ConfigManager.sync(DOMAIN, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void registerItem(ModelRegistryEvent event)
    {
        ResourceLocation[] resLocs = new ResourceLocation[6];
        String[] types = {"_blank","_shine","_glow"};
        for (String type:types){
            resLocs[0] = new ModelResourceLocation(DOMAIN+";"+itemMirror.getRegistryName()+"_silver"+type,"inventory");
            resLocs[1] = new ModelResourceLocation(DOMAIN+";"+itemMirror.getRegistryName()+"_gold"+type,"inventory");
            resLocs[2] = new ModelResourceLocation(DOMAIN+";"+itemMirror.getRegistryName()+"_diamond"+type,"inventory");
            resLocs[3] = new ModelResourceLocation(DOMAIN+";"+itemMirror.getRegistryName()+"_silver_dirty"+type,"inventory");
            resLocs[4] = new ModelResourceLocation(DOMAIN+";"+itemMirror.getRegistryName()+"_gold_dirty"+type,"inventory");
            resLocs[5] = new ModelResourceLocation(DOMAIN+";"+itemMirror.getRegistryName()+"_diamond_dirty"+type,"inventory");
        }
        ModelLoader.setCustomMeshDefinition(itemMirror,new ModelMirror(itemMirror));
        ModelBakery.registerItemVariants(itemMirror,resLocs);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Created item
        itemMirror = new ItemMirror();

        //Load configs
        EntityData.MAX_TELEPORT_DISTANCE = ConfigHandler.Max_Teleport_Distance;
        ItemMirror.TICKS_BEFORE_TELEPORT = ConfigHandler.Activation_Time;
        CapabilityManager.INSTANCE.register(IMirrorData.class,new MirrorStorage(),MirrorData.class);

        network = NetworkRegistry.INSTANCE.newSimpleChannel(DOMAIN);
        network.registerMessage(PacketClientUpdate.Handler.class,PacketClientUpdate.class,1, Side.CLIENT);
    }
}