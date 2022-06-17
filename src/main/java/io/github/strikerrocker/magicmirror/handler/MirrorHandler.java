package io.github.strikerrocker.magicmirror.handler;

import io.github.strikerrocker.magicmirror.MagicMirror;
import io.github.strikerrocker.magicmirror.capability.CapabilitySerializerProvider;
import io.github.strikerrocker.magicmirror.config.ConfigCost;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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


    private MirrorHandler() {
    }

    /**
     * Updates tracked information about the player
     */
    public static void updateUserData(Player player, ItemStack mirror) {
        get(player).update(player, mirror);
    }

    public static EntityData get(Player player) {
        final UUID id = player.getGameProfile().getId();
        if (!userData.containsKey(id)) {
            userData.put(player.getUUID(), new EntityData(player));
        }
        return userData.get(player.getUUID());
    }

    /**
     * Called to teleport the player to his set mirror location
     */
    public static void teleport(Player player) {
        player.getCapability(MagicMirror.CAPABILITY_MIRROR).ifPresent(mirrorData -> {
            if (mirrorData.hasLocation()) {
                player.level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 2F);
                mirrorData.getLocation().teleport(player);
                TeleportPos teleportPos = mirrorData.getLocation();
                RandomSource random = player.getRandom();
                player.level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 2F);
                if (player.level.isClientSide()) {
                    player.level.addParticle(ParticleTypes.PORTAL, teleportPos.x, teleportPos.y, teleportPos.z, (random.nextDouble() - 0.5D) * 2.0D, (random.nextDouble() - 0.5D) * 2.0D, (random.nextDouble() - 0.5D) * 2.0D);
                }
                if (ConfigCost.USE_XP.get()) {
                    player.giveExperiencePoints((int) -mirrorData.getLocation().getTeleportCost(player));
                }
            }
            mirrorData.setLocation(null);
        });
    }

    @SubscribeEvent
    public static void onChangeDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof Player player) {
            get(player).reset();
            player.getCapability(MagicMirror.CAPABILITY_MIRROR).ifPresent(iMirrorData -> iMirrorData.setLocation(null));
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(CAP_KEY, new CapabilitySerializerProvider());
        }
    }
}