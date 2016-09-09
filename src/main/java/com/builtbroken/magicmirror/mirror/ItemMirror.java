package com.builtbroken.magicmirror.mirror;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.handler.MirrorHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Item class for the basic magic mirror
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
public class ItemMirror extends Item
{
    public static final int TICKS_BEFORE_TELEPORT = 5 * 20; //5 seconds TODO load from config

    public ItemMirror()
    {
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName(MagicMirror.DOMAIN + ":magicMirror");
        setTextureName(MagicMirror.DOMAIN + ":magicMirror");
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
        if (count <= 1)
        {
            MirrorHandler.teleport(player);
            player.stopUsingItem();
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return TICKS_BEFORE_TELEPORT + 1;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(player.getItemInUse() == null)
        {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean b)
    {
        //TODO add a way to disable users from using the mirror, CONFIG?
        if (entity instanceof EntityPlayer)
        {
            MirrorHandler.updateUserData((EntityPlayer) entity);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if(player.worldObj.provider.hasNoSky)
        {
            list.add("\u00a7c" + StatCollector.translateToLocal(getUnlocalizedName() + ".error.noSky"));
        }
        else
        {
            list.add(StatCollector.translateToLocal(getUnlocalizedName() + ".desc"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack p_77636_1_)
    {
        //TODO if has teleport location and XP add glow effect
        return p_77636_1_.isItemEnchanted();
    }

    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_)
    {
        return EnumRarity.rare;
    }
}
