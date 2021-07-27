package io.github.strikerrocker.magicmirror.handler;

import io.github.strikerrocker.magicmirror.MagicMirror;
import io.github.strikerrocker.magicmirror.capability.IMirrorData;
import io.github.strikerrocker.magicmirror.capability.MirrorData;
import io.github.strikerrocker.magicmirror.config.ConfigCost;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;


/**
 * Handles everything to do with the mirror activation and tracking
 */
@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN)
public class MirrorHandler {
    /**
     * Username to teleport location, saves when game closes
     */
    private static final HashMap<UUID, EntityData> userData = new HashMap<>();

    private static final ResourceLocation CAP_KEY = new ResourceLocation(MagicMirror.DOMAIN, "teleport_position");

    private static final LazyOptional<IMirrorData> holder = LazyOptional.of(MirrorData::new);

    private MirrorHandler() {
    }

    /**
     * Updates tracked information about the player
     */
    public static void updateUserData(PlayerEntity player, ItemStack mirror) {
        get(player).update(player, mirror);
    }

    public static EntityData get(PlayerEntity player) {
        final UUID id = player.getGameProfile().getId();
        if (!userData.containsKey(id)) {
            userData.put(player.getUUID(), new EntityData(player));
        }
        return userData.get(player.getUUID());
    }

    /**
     * Called to teleport the player to his set mirror location
     */
    public static void teleport(PlayerEntity player) {
        player.getCapability(MagicMirror.CAPABILITY_MIRROR).ifPresent(mirrorData -> {
            if (mirrorData.hasLocation()) {
                mirrorData.getLocation().teleport(player);
                if (ConfigCost.USE_XP.get()) {
                    player.giveExperiencePoints((int) -mirrorData.getLocation().getTeleportCost(player));
                }
            }
            mirrorData.setLocation(null);
        });
    }

    @SubscribeEvent
    public static void onChangeDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            get(player).reset();
            player.getCapability(MagicMirror.CAPABILITY_MIRROR).ifPresent(iMirrorData -> iMirrorData.setLocation(null));
        }
    }


    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(CAP_KEY, new ICapabilityProvider() {
                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    return MagicMirror.CAPABILITY_MIRROR.orEmpty(cap, holder);
                }
            });
        }
    }
}