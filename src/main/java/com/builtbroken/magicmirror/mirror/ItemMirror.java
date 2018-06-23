package com.builtbroken.magicmirror.mirror;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.handler.MirrorHandler;
import com.builtbroken.magicmirror.handler.capability.IMirrorData;
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

import javax.annotation.Nullable;
import java.util.List;

import static com.builtbroken.magicmirror.MagicMirror.proxy;
import static com.builtbroken.magicmirror.handler.capability.MirrorStorage.CAPABILITY_MIRROR;

/**
 * Item class for the basic magic mirror
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
public class ItemMirror extends Item
{
    public static int TICKS_BEFORE_TELEPORT = 5 * 20; //5 seconds
    /** CLIENT DATA, how much XP it costs to teleport */
    public static int currentXPCostToTeleport = 0;
    /** CLIENT DATA,  0 = nothing, 1 = can be activated, 2 = is charged / will record data, 3 = is charged & can be activated*/
    public static byte currentMirrorState = 0;

    public ItemMirror()
    {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.TOOLS);
        setUnlocalizedName(MagicMirror.DOMAIN + ":magicmirror");
        setRegistryName(MagicMirror.DOMAIN + ":magicmirror");
        proxy.registerItemRenderer(this,0,"silver");
        proxy.registerItemRenderer(this,1,"gold");
        proxy.registerItemRenderer(this,2,"diamond");
        proxy.registerItemRenderer(this,3,"silver_dirty");
        proxy.registerItemRenderer(this,4,"gold_dirty");
        proxy.registerItemRenderer(this,5,"diamond_dirty");
    }

    public static IMirrorData getHandler(EntityPlayer entity) {

        if (entity.hasCapability(CAPABILITY_MIRROR, EnumFacing.DOWN))
            return entity.getCapability(CAPABILITY_MIRROR, EnumFacing.DOWN);
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getMetadata();
        switch (i)
        {
            case 1:
                return "gold_icon";
            case 2:
                return "diamond_icon";
            case 3:
                return "silver_dirty_icon";
            case 4:
                return "gold_dirty_icon";
            case 5:
                return "diamond_dirty_icon";
        }
        return "silver_clean";
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return TICKS_BEFORE_TELEPORT + 1;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        //TODO play charging sound effect
        if (count <= 1)
        {
            MirrorHandler.teleport((EntityPlayer)player);
            player.stopActiveHand();
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
                if (playerIn.getHeldItem(handIn) == ItemStack.EMPTY)
                {
                        playerIn.setActiveHand(handIn);
                }
            }
            else if (!getHandler(playerIn).hasLocation())
            {
                playerIn.sendStatusMessage(new TextComponentTranslation("item.smbmagicmirror:magicmirror.error.nolocation"),true);
            }
            else if (getHandler(playerIn).getXpTeleportCost(playerIn) > playerIn.experienceTotal)
            {
                String translation = I18n.translateToLocal("item.smbmagicmirror:magicmirror.error.xp");
                translation = translation.replace("%1", "" + (getHandler(playerIn).getXpTeleportCost(playerIn) - playerIn.experienceTotal));
                translation = translation.replace("%2", "" + getHandler(playerIn).getXpTeleportCost(playerIn));
                playerIn.sendStatusMessage(new TextComponentString(translation),true);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
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
        float xp = getHandler(player).getXpTeleportCost(player);
        return (int) xp > 0 && (!MagicMirror.USE_XP || player.capabilities.isCreativeMode || player.experienceTotal >= xp);
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
            if (worldIn.provider.hasSkyLight())
            {
                sep("\u00a7c", I18n.translateToLocal(getUnlocalizedName() + ".error.nosky"), tooltip);
            }
            else
            {
                sep(I18n.translateToLocal(getUnlocalizedName() + ".desc"), tooltip);
            }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.isItemEnchanted();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
        items.add(new ItemStack(this, 1, 2));
        items.add(new ItemStack(this, 1, 3));
        items.add(new ItemStack(this, 1, 4));
        items.add(new ItemStack(this, 1, 5));
    }
}