package com.builtbroken.magicmirror.handler;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.capability.IMirrorData;
import com.builtbroken.magicmirror.capability.MirrorProvider;
import com.builtbroken.magicmirror.capability.MirrorStorage;
import com.builtbroken.magicmirror.config.ConfigCost;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;


/**
 * Handles everything to do with the mirror activation and tracking
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN)
public class MirrorHandler
{
    /**
     * Username to teleport location, saves when game closes
     */
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
    public static void updateUserData(EntityPlayer player, ItemStack mirror)
    {
        get(player).update(player, mirror);
    }

    public static EntityData get(EntityPlayer player)
    {
        final UUID id = player.getGameProfile().getId();
        if (!userData.containsKey(id)) {
            userData.put(player.getUniqueID(), new EntityData(player));
        }
        return userData.get(player.getUniqueID());
    }

    public static IMirrorData getData(EntityPlayer entity)
    {
        return entity.getCapability(MirrorStorage.CAPABILITY_MIRROR).orElse(null);
    }

    /**
     * Called to teleport the player to his set mirror location
     *
     * @param player - player, not null
     */
    public static void teleport(EntityPlayer player)
    {
        player.getCapability(MirrorStorage.CAPABILITY_MIRROR).ifPresent(iMirrorData -> {
            if (iMirrorData.hasLocation()) {
                iMirrorData.getLocation().teleport(player);
            }
        });
        if (ConfigCost.USE_XP.get()) {
            player.giveExperiencePoints((int) -getData(player).getLocation().getTeleportCost(player));
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(EntityTravelToDimensionEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            get(player).reset();
            player.getCapability(MirrorStorage.CAPABILITY_MIRROR).ifPresent(iMirrorData -> iMirrorData.setLocation(null));
        }
    }


    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(CAP_KEY, new MirrorProvider());
        }
    }
}