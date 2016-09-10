package com.builtbroken.magicmirror;

import com.builtbroken.magicmirror.handler.EntityData;
import com.builtbroken.magicmirror.handler.MirrorHandler;
import com.builtbroken.magicmirror.mirror.ItemMirror;
import com.builtbroken.magicmirror.network.PacketManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Very simple mod to help users get back to the start of a dungeon after clear it. The mod works by tracking the user's position and recording the last
 * position the user was before going underground. Then when users the mirror will teleport the user to the last recorded position.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
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

    /** Config object used to load settings */
    public static Configuration config;

    public static PacketManager packetHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Magic_Mirror.cfg"));
        config.load();

        //Register event
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(MirrorHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(MirrorHandler.INSTANCE);

        //Created item
        itemMirror = new ItemMirror();
        GameRegistry.registerItem(itemMirror, "magicMirrorSBM");

        //Load configs
        EntityData.MAX_TELEPORT_DISTANCE = config.getInt("Max_Teleport_Distance", Configuration.CATEGORY_GENERAL, EntityData.MAX_TELEPORT_DISTANCE, -1, 9999999, "Sets the max distance for the mirror to continue to save and allow teleportion");
        ItemMirror.TICKS_BEFORE_TELEPORT = config.getInt("Activation_Time", Configuration.CATEGORY_GENERAL, ItemMirror.TICKS_BEFORE_TELEPORT, -1, 1200, "How long in ticks the user has to wait before teleporting");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        packetHandler = new PacketManager(DOMAIN);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //TODO add loot drop
        //TODO add config to tweek drop rate
        //TODO add config to disable drop rate
        //TODO monster drops (super rare, .0003) and chest drop(common, 0.15)

        config.save();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        //TODO add admin command to clear location of mirror
        //TODO add admin command to activate mirror
        //TODO add admin command to  set location
    }
}
