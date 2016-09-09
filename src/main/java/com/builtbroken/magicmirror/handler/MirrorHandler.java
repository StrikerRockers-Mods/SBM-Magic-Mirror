package com.builtbroken.magicmirror.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles everything to do with the mirror activation and tracking
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
public class MirrorHandler
{
    /** Username to teleport location, saves when game closes */
    public static final HashMap<UUID, TeleportPos> userIDToMirrorLocation = new HashMap();
    public static final HashMap<UUID, EntityData> userData = new HashMap();

    public static final MirrorHandler INSTANCE = new MirrorHandler();

    private MirrorHandler()
    {
    }

    /**
     * Updates tracked information about the player
     *
     * @param player
     */
    public static void updateUserData(EntityPlayer player)
    {
        final UUID id = player.getGameProfile().getId();
        if (!userData.containsKey(id))
        {
            userData.put(player.getUniqueID(), new EntityData(player));
        }
        userData.get(player.getUniqueID()).update(player);
    }


    /**
     * Sets the player's teleport location
     *
     * @param player
     * @param potentialTP
     */
    public static void setTeleportLocation(EntityPlayer player, TeleportPos potentialTP)
    {
        if (potentialTP == null)
        {
            userIDToMirrorLocation.remove(player.getGameProfile().getId());
        }
        else
        {
            String translation =  StatCollector.translateToLocal("item.smbmagicmirror:magicMirror.location.set").replace("%1", "" + potentialTP.x).replace("%2", "" + potentialTP.y).replace("%3", "" + potentialTP.z);
            if(translation != null)
            {
                player.addChatComponentMessage(new ChatComponentText(translation));
            }
            userIDToMirrorLocation.put(player.getGameProfile().getId(), potentialTP);
        }
    }


    /**
     * Called to teleport the player to his set mirror location
     *
     * @param player - player, not null
     */
    public static void teleport(EntityPlayer player)
    {
        if (userIDToMirrorLocation.containsKey(player.getGameProfile().getId()))
        {
            userIDToMirrorLocation.get(player.getGameProfile().getId()).teleport(player);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        clearData(event.player);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            clearData((EntityPlayer) event.entity);
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        //TODO don't clear for hell dim, rather set location to in front of hell door
        clearData(event.player);
    }

    /**
     * Removes the player's location for teleportation
     *
     * @param player - NPE checked, NPE checked for {@link com.mojang.authlib.GameProfile}
     */
    public static void clearData(EntityPlayer player)
    {
        if (player != null && player.getGameProfile() != null)
        {
            UUID id = player.getGameProfile().getId();
            if (userIDToMirrorLocation.containsKey(id))
            {
                userIDToMirrorLocation.remove(id);
                userData.remove(id);
            }
        }
    }

    /**
     * Checks if the player has a location stored for use
     *
     * @param e
     * @return
     */
    public static boolean hasLocation(EntityPlayer e)
    {
        return userIDToMirrorLocation.containsKey(e.getGameProfile().getId());
    }

    /**
     * Gets the location to teleport the user to
     *
     * @param e
     * @return
     */
    public static TeleportPos getLocation(EntityPlayer e)
    {
        return userIDToMirrorLocation.get(e.getGameProfile().getId());
    }
}
