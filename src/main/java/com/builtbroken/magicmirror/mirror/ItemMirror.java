package com.builtbroken.magicmirror.mirror;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.capability.IMirrorData;
import com.builtbroken.magicmirror.config.ConfigCost;
import com.builtbroken.magicmirror.config.ConfigUse;
import com.builtbroken.magicmirror.handler.MirrorHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public static int currentXPCostToTeleport = 0;
    /**
     * CLIENT DATA,  0 = nothing, 1 = can be activated, 2 = is charged / will record data, 3 = is charged & can be activated
     */
    public static MirrorState currentMirrorState = MirrorState.DEFAULT;

    public ItemMirror()
    {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.TOOLS);
        setUnlocalizedName(MagicMirror.DOMAIN + ":magicmirror");
        setRegistryName(MagicMirror.DOMAIN + ":magicmirror");
    }

    public static IMirrorData getHandler(EntityPlayer entity)
    {
        if (entity.hasCapability(CAPABILITY_MIRROR, EnumFacing.DOWN))
        {
            return entity.getCapability(CAPABILITY_MIRROR, EnumFacing.DOWN);
        }
        return null;
    }

    public static void sep(String translation, List list)
    {
        sep(null, translation, list);
    }

    public static void sep(String color, String translation, List list)
    {
        if (translation != null && !translation.isEmpty())
        {
            String[] strings = translation.split(",");
            for (String s : strings)
            {
                list.add((color != null ? color : "") + s.trim());
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return ConfigUse.TICKS_BEFORE_TELEPORT + 1;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
        //TODO play charging sound effect
        if (getMaxItemUseDuration(stack) - count >= 1 && player instanceof EntityPlayer)
        {
            MirrorHandler.teleport((EntityPlayer) player);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (!worldIn.isRemote)
        {
            if (canTeleport(playerIn))
            {
                //TODO play charge start sound effect
                playerIn.setActiveHand(handIn);
                return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            }
            else if (!getHandler(playerIn).hasLocation())
            {
                playerIn.sendStatusMessage(new TextComponentTranslation("item.sbmmagicmirror:magicmirror.error.nolocation"), true);
                return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
            }
            else if (getHandler(playerIn).getXpTeleportCost(playerIn) > playerIn.experienceTotal)
            {
                String translation = I18n.translateToLocal("item.sbmmagicmirror:magicmirror.error.xp");
                translation = translation.replace("%1", "" + (getHandler(playerIn).getXpTeleportCost(playerIn) - playerIn.experienceTotal));
                translation = translation.replace("%2", "" + getHandler(playerIn).getXpTeleportCost(playerIn));
                playerIn.sendStatusMessage(new TextComponentString(translation), true);
                return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean b)
    {
        //TODO play sound effect when mirror is ready to use (has location, user has XP)
        //TODO play sound effect when mirror has charged(Is able to store a location)
        //TODO play sound effect when mirror loses charge or location (User leaves teleport area)
        //TODO add a way to disable users from using the mirror, CONFIG?
        if (entity instanceof EntityPlayer)
        {
            MirrorHandler.updateUserData((EntityPlayer) entity);
        }
    }

    /**
     * Can the user teleport
     *
     * @param player
     * @return
     */
    public boolean canTeleport(EntityPlayer player)
    {
        //Ignore cost if XP use is disabled or player is in creative mode
        if (!ConfigCost.USE_XP || player.capabilities.isCreativeMode)
        {
            return true;
        }

        //If normal player and config, check for XP cost
        float xp = getHandler(player).getXpTeleportCost(player);
        return player.experienceTotal >= xp;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (worldIn != null)
        {
            if (!worldIn.provider.hasSkyLight())
            {
                sep("\u00a7c", net.minecraft.client.resources.I18n.format(getUnlocalizedName() + ".error.nosky"), tooltip);
            }
            else
            {
                sep(net.minecraft.client.resources.I18n.format(getUnlocalizedName() + ".desc"), tooltip);
            }

            if(MagicMirror.runningAsDev)
            {
                tooltip.add("" + currentXPCostToTeleport);
                tooltip.add("" + currentMirrorState);
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return stack.isItemEnchanted();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.RARE;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (tab == CreativeTabs.TOOLS)
        {
            for (MirrorSubType type : MirrorSubType.values())
            {
                items.add(new ItemStack(this, 1, type.ordinal()));
            }
        }
    }
}