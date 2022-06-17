package io.github.strikerrocker.magicmirror.mirror;

import io.github.strikerrocker.magicmirror.MagicMirror;
import io.github.strikerrocker.magicmirror.config.ConfigCost;
import io.github.strikerrocker.magicmirror.config.ConfigUse;
import io.github.strikerrocker.magicmirror.handler.MirrorHandler;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static io.github.strikerrocker.magicmirror.MagicMirror.CAPABILITY_MIRROR;

/**
 * Item class for the basic magic mirror
 */
public class MirrorItem extends Item {
    /**
     * CLIENT DATA, how much XP it costs to teleport
     */
    public float currentXPCostToTeleport = 0;
    /**
     * CLIENT DATA,  0 = nothing, 1 = can be activated, 2 = is charged / will record data, 3 = is charged & can be activated
     */
    public MirrorState currentMirrorState = MirrorState.DEFAULT;

    public MirrorItem(MirrorSubType type) {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
        ItemProperties.register(this, new ResourceLocation(MagicMirror.DOMAIN, "state"), (stack, world, entity, no) -> entity instanceof Player ? getState((Player) entity) : 0);
    }

    public float getState(Player player) {
        boolean isCharged = MirrorHandler.get(player).timeAboveGround >= ConfigUse.MIN_SURFACE_TIME.get();
        if (!player.getCapability(CAPABILITY_MIRROR).isPresent()) {
            return 0;
        }
        boolean isActive = player.getCapability(CAPABILITY_MIRROR).orElse(null).hasLocation();
        return (isCharged && isActive ? 3 : isCharged ? 2 : isActive ? 1 : 0);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if (entity instanceof Player) {
            MirrorHandler.updateUserData((Player) entity, stack);
        }
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return ConfigUse.TICKS_BEFORE_TELEPORT.get() + 1;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (getUseDuration(stack) - count >= 1 && player instanceof Player) {
            MirrorHandler.teleport((Player) player);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide && playerIn.getCapability(CAPABILITY_MIRROR).isPresent()) {
            if (canTeleport(playerIn)) {
                playerIn.startUsingItem(handIn);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
            } else if (!playerIn.getCapability(CAPABILITY_MIRROR).orElse(null).hasLocation()) {
                playerIn.displayClientMessage(Component.translatable("item.sbmmagicmirror:magicmirror.error.nolocation"), true);
                return new InteractionResultHolder<>(InteractionResult.FAIL, playerIn.getItemInHand(handIn));
            } else if (playerIn.getCapability(CAPABILITY_MIRROR).orElse(null).getLocation().getTeleportCost(playerIn) > playerIn.totalExperience) {
                int needed_xp = (int) Math.ceil(playerIn.getCapability(CAPABILITY_MIRROR).orElse(null).getLocation().getTeleportCost(playerIn));
                int missing_xp = needed_xp - playerIn.totalExperience;

                playerIn.displayClientMessage(Component.translatable(
                                "item.sbmmagicmirror:magicmirror.error.xp",
                                missing_xp,
                                needed_xp),
                        true);
                return new InteractionResultHolder<>(InteractionResult.FAIL, playerIn.getItemInHand(handIn));
            }
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, playerIn.getItemInHand(handIn));
    }

    /**
     * Can the user teleport
     */
    private boolean canTeleport(Player player) {
        //Ignore cost if XP use is disabled or player is in creative mode
        if (!ConfigCost.USE_XP.get() || player.isCreative()) {
            return true;
        }
        if (!player.getCapability(CAPABILITY_MIRROR).orElse(null).hasLocation())
            return false;

        //If normal player and config, check for XP cost
        float xp = player.getCapability(CAPABILITY_MIRROR).orElse(null).getLocation().getTeleportCost(player);
        return player.totalExperience >= xp;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (worldIn != null) {
            if (!worldIn.dimensionType().hasSkyLight()) {
                tooltip.add(Component.translatable("item.sbmmagicmirror:magicmirror.error.nosky"));
            } else {
                tooltip.add(Component.translatable("item.sbmmagicmirror:magicmirror.desc" + (ConfigCost.USE_XP.get() ? ".xp" : "")));
            }
            if (tooltipFlag.isAdvanced()) {
                tooltip.add(Component.literal(currentXPCostToTeleport + ""));
                tooltip.add(Component.literal((currentMirrorState + "")));
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.isEnchanted();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }
}