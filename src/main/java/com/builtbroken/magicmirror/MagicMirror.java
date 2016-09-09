package com.builtbroken.magicmirror;

import com.builtbroken.magicmirror.handler.MirrorHandler;
import com.builtbroken.magicmirror.mirror.ItemMirror;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Very simple mod to help users get back to the start of a dungeon after clear it. The mod works by tracking the user's position and recording the last
 * position the user was before going underground. Then when users the mirror will teleport the user to the last recorded position.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
@Mod(modid = MagicMirror.DOMAIN, name = "SMB Magic Mirror", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class MagicMirror
{
    //TODO track position
    //TODO ignore all position that can't see sky
    //TODO ignore all positions bellow see level, should fix user being seen inside holes and in canyons
    //TODO ensure user has been above ground longer than a few mins, TWO positions (Best, and Last resort)
    //TODO ignore trees and see threw blocks to improve canSeeSky()

    //TODO after finished
    //TODO make the mirror glow when it can be used
    //TODO make it have a visual change when it has a stored location
    //TODO show some kind of progress bar for XP needed


    //TODO Step 1 - Build the init
    //TODO      ModClass
    //TODO      ClientProxy
    //TODO      CommonProxy
    //TODO      ServerProxy     - Handle server side only stuff, commands

    //TODO Step 2 - Make the item
    //TODO      Item.class
    //TODO      Right Click Method
    //TODO      Texture

    //TODO Step 3 - Build the player tracker
    //TODO      Storing the position
    //TODO      Event Handling(Dies, Respawns, Moves, Jumps)
    //TODO      Event to fire(Marks position, etc) -- optional

    public static final boolean runningAsDev = System.getProperty("development") != null && System.getProperty("development").equalsIgnoreCase("true");
    public static final Logger logger = LogManager.getLogger("SBM-MagicMirror");

    public static final String DOMAIN = "smbmagicmirror";

    @Mod.Instance(DOMAIN)
    public static MagicMirror INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.magicmirror.ClientProxy", serverSide = "com.builtbroken.magicmirror.ServerProxy")
    public static CommonProxy proxy;

    public static ItemMirror itemMirror;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Register event
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(MirrorHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(MirrorHandler.INSTANCE);

        //Created item
        itemMirror = new ItemMirror();
        GameRegistry.registerItem(itemMirror, "magicMirrorSBM");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //TODO add loot drop
        //TODO add config to tweek drop rate
        //TODO add config to disable drop rate
        //TODO monster drops (super rare, .0003) and chest drop(common, 0.15)
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        //TODO add admin command to clear location of mirror
        //TODO add admin command to activate mirror
        //TODO add admin command to  set location
    }
}
