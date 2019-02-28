package com.builtbroken.magicmirror.mirror;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.config.ConfigCost;
import com.builtbroken.magicmirror.config.ConfigUse;
import com.builtbroken.magicmirror.handler.MirrorHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static com.builtbroken.magicmirror.capability.MirrorStorage.CAPABILITY_MIRROR;

/**
 * Item class for the basic magic mirror
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
public class ItemMirror extends Item
{
    /**
     * CLIENT DATA, how much XP it costs to teleport
     */
    public float currentXPCostToTeleport = 0;
    /**
     * CLIENT DATA,  0 = nothing, 1 = can be activated, 2 = is charged / will record data, 3 = is charged & can be activated
     */
    public MirrorState currentMirrorState = MirrorState.DEFAULT;

    public ItemMirror(MirrorSubType type)
    {
        super(new Properties().maxStackSize(1).group(ItemGroup.TOOLS));
        setRegistryName(MagicMirror.DOMAIN, "magicmirror_" + type.toString().toLowerCase());
        addPropertyOverride(new ResourceLocation(MagicMirror.DOMAIN, "state"), (stack, world, entity) -> entity instanceof EntityPlayer ? getState((EntityPlayer) entity) : 0);
    }

    public float getState(EntityPlayer player)
    {
        boolean isCharged = MirrorHandler.get(player).timeAboveGround >= ConfigUse.MIN_SURFACE_TIME.get();
        if (!player.getCapability(CAPABILITY_MIRROR).isPresent()) {
            return 0;
        }
        boolean isActive = MirrorHandler.getData(player).hasLocation();
        return (isCharged && isActive ? 3 : isCharged ? 2 : isActive ? 1 : 0);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_)
    {
        //TODO play sound effect when mirror is ready to use (has location, user has XP)
        //TODO play sound effect when mirror has charged(Is able to store a location)
        //TODO play sound effect when mirror loses charge or location (User leaves teleport area)
        //TODO add a way to disable users from using the mirror, CONFIG?
        if (entity instanceof EntityPlayer) {
            MirrorHandler.updateUserData((EntityPlayer) entity, stack);
        }
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_)
    {
        return ConfigUse.TICKS_BEFORE_TELEPORT.get() + 1;

    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
        //TODO play charging sound effect
        if (getUseDuration(stack) - count >= 1 && player instanceof EntityPlayer) {
            MirrorHandler.teleport((EntityPlayer) player);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (!worldIn.isRemote && MirrorHandler.getData(playerIn) != null) {
            if (canTeleport(playerIn)) {
                //TODO play charge start sound effect
                playerIn.setActiveHand(handIn);
                return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            } else if (!MirrorHandler.getData(playerIn).hasLocation()) {
                playerIn.sendStatusMessage(new TextComponentTranslation("item.sbmmagicmirror:magicmirror.error.nolocation"), true);
                return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
            } else if (MirrorHandler.getData(playerIn).getLocation().getTeleportCost(playerIn) > playerIn.experienceTotal) {
                int needed_xp = (int) Math.ceil(MirrorHandler.getData(playerIn).getLocation().getTeleportCost(playerIn));
                int missing_xp = needed_xp - playerIn.experienceTotal;

                playerIn.sendStatusMessage(new TextComponentTranslation(
                                "item.sbmmagicmirror:magicmirror.error.xp",
                                missing_xp,
                                needed_xp),
                        true);
                return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    }

    /**
     * Can the user teleport
     */
    private boolean canTeleport(EntityPlayer player)
    {
        //Ignore cost if XP use is disabled or player is in creative mode
        if (!ConfigCost.USE_XP.get() || player.abilities.isCreativeMode) {
            return true;
        }

        //If normal player and config, check for XP cost
        float xp = MirrorHandler.getData(player).getLocation().getTeleportCost(player);
        return player.experienceTotal >= xp;
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_)
    {
        if (worldIn != null) {
            if (!worldIn.dimension.hasSkyLight()) {
                tooltip.add(new TextComponentTranslation("item.sbmmagicmirror:magicmirror.error.nosky"));
            } else {
                tooltip.add(new TextComponentTranslation("item.sbmmagicmirror:magicmirror.desc" + (ConfigCost.USE_XP.get() ? ".xp" : "")));
            }

            if (MagicMirror.runningAsDev) {
                tooltip.add(new TextComponentString(currentXPCostToTeleport + ""));
                tooltip.add(new TextComponentString(currentMirrorState + ""));
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return stack.isEnchanted();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.RARE;
    }
}