package io.github.strikerrocker.magicmirror.mirror;

import io.github.strikerrocker.magicmirror.MagicMirror;
import io.github.strikerrocker.magicmirror.config.ConfigCost;
import io.github.strikerrocker.magicmirror.config.ConfigUse;
import io.github.strikerrocker.magicmirror.handler.MirrorHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
        super(new Properties().maxStackSize(1).group(ItemGroup.TOOLS));
        setRegistryName(MagicMirror.DOMAIN, "magicmirror_" + type.toString().toLowerCase());
        ItemModelsProperties.func_239418_a_(this, new ResourceLocation(MagicMirror.DOMAIN, "state"), (stack, world, entity) -> entity instanceof PlayerEntity ? getState((PlayerEntity) entity) : 0);
    }

    public float getState(PlayerEntity player) {
        boolean isCharged = MirrorHandler.get(player).timeAboveGround >= ConfigUse.MIN_SURFACE_TIME.get();
        if (!player.getCapability(CAPABILITY_MIRROR).isPresent()) {
            return 0;
        }
        boolean isActive = player.getCapability(CAPABILITY_MIRROR).orElse(null).hasLocation();
        return (isCharged && isActive ? 3 : isCharged ? 2 : isActive ? 1 : 0);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        //TODO play sound effect when mirror is ready to use (has location, user has XP)
        //TODO play sound effect when mirror has charged(Is able to store a location)
        //TODO play sound effect when mirror loses charge or location (User leaves teleport area)
        if (entity instanceof PlayerEntity) {
            MirrorHandler.updateUserData((PlayerEntity) entity, stack);
        }
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return ConfigUse.TICKS_BEFORE_TELEPORT.get() + 1;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        //TODO play charging sound effect
        if (getUseDuration(stack) - count >= 1 && player instanceof PlayerEntity) {
            MirrorHandler.teleport((PlayerEntity) player);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote && playerIn.getCapability(CAPABILITY_MIRROR).isPresent()) {
            if (canTeleport(playerIn)) {
                //TODO play charge start sound effect
                playerIn.setActiveHand(handIn);
                return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
            } else if (!playerIn.getCapability(CAPABILITY_MIRROR).orElse(null).hasLocation()) {
                playerIn.sendStatusMessage(new TranslationTextComponent("item.sbmmagicmirror:magicmirror.error.nolocation"), true);
                return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
            } else if (playerIn.getCapability(CAPABILITY_MIRROR).orElse(null).getLocation().getTeleportCost(playerIn) > playerIn.experienceTotal) {
                int needed_xp = (int) Math.ceil(playerIn.getCapability(CAPABILITY_MIRROR).orElse(null).getLocation().getTeleportCost(playerIn));
                int missing_xp = needed_xp - playerIn.experienceTotal;

                playerIn.sendStatusMessage(new TranslationTextComponent(
                                "item.sbmmagicmirror:magicmirror.error.xp",
                                missing_xp,
                                needed_xp),
                        true);
                return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
            }
        }
        return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
    }

    /**
     * Can the user teleport
     */
    private boolean canTeleport(PlayerEntity player) {
        //Ignore cost if XP use is disabled or player is in creative mode
        if (!ConfigCost.USE_XP.get() || player.abilities.isCreativeMode) {
            return true;
        }
        if (!player.getCapability(CAPABILITY_MIRROR).orElse(null).hasLocation())
            return false;

        //If normal player and config, check for XP cost
        float xp = player.getCapability(CAPABILITY_MIRROR).orElse(null).getLocation().getTeleportCost(player);
        return player.experienceTotal >= xp;
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        if (worldIn != null) {
            if (!worldIn.func_230315_m_().hasSkyLight()) {
                tooltip.add(new TranslationTextComponent("item.sbmmagicmirror:magicmirror.error.nosky"));
            } else {
                tooltip.add(new TranslationTextComponent("item.sbmmagicmirror:magicmirror.desc" + (ConfigCost.USE_XP.get() ? ".xp" : "")));
            }
            if (MagicMirror.runningAsDev) {
                tooltip.add(new StringTextComponent(currentXPCostToTeleport + ""));
                tooltip.add(new StringTextComponent((currentMirrorState + "")));
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.isEnchanted();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }
}