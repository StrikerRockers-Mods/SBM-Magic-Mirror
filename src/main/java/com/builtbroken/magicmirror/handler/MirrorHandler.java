package com.builtbroken.magicmirror.handler;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.capability.MirrorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles everything to do with the mirror activation and tracking
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
@Mod.EventBusSubscriber
public class MirrorHandler
{
    /**
     * Username to teleport location, saves when game closes
     */
    public static final HashMap<UUID, TeleportPos> userIDToMirrorLocation = new HashMap();
    public static final HashMap<UUID, EntityData> userData = new HashMap();

    public static final ResourceLocation CAP_KEY = new ResourceLocation(MagicMirror.DOMAIN, "teleport_position");

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
        get(player).update(player);
    }

    public static EntityData get(EntityPlayer player)
    {
        final UUID id = player.getGameProfile().getId();
        if (!userData.containsKey(id))
        {
            userData.put(player.getUniqueID(), new EntityData(player));
        }
        return userData.get(player.getUniqueID());
    }

    /**
     * Called to teleport the player to his set mirror location
     *
     * @param player - player, not null
     */
    public static void teleport(EntityPlayer player)
    {
        if (EntityData.getHandler(player).hasLocation()) {
            EntityData.getHandler(player).getLocation().teleport(player);
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(CAP_KEY, new MirrorProvider());
        }
    }
}
