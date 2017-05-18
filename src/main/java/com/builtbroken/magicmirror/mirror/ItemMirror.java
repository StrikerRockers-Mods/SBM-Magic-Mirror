package com.builtbroken.magicmirror.mirror;

import com.builtbroken.magicmirror.MagicMirror;
import com.builtbroken.magicmirror.handler.MirrorHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
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
    public static int TICKS_BEFORE_TELEPORT = 5 * 20; //5 seconds
    /** CLIENT DATA, how much XP it costs to teleport */
    public static int currentXPCostToTeleport = 0;
    /** CLIENT DATA,  0 = nothing, 1 = can be activated, 2 = is charged / will record data, 3 = is charged & can be activated*/
    public static byte currentMirrorState = 0;

    @SideOnly(Side.CLIENT)
    private IIcon silver_dirty_icon;

    @SideOnly(Side.CLIENT)
    private IIcon gold_dirty_icon;

    @SideOnly(Side.CLIENT)
    private IIcon diamond_dirty_icon;

    @SideOnly(Side.CLIENT)
    private IIcon gold_icon;

    @SideOnly(Side.CLIENT)
    private IIcon diamond_icon;

    @SideOnly(Side.CLIENT)
    private IIcon glow_icon;

    @SideOnly(Side.CLIENT)
    private IIcon shine_icon;

    @SideOnly(Side.CLIENT)
    private IIcon blank_icon;

    public ItemMirror()
    {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName(MagicMirror.DOMAIN + ":magicMirror");
        setTextureName(MagicMirror.DOMAIN + ":Silver_Clean");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(MagicMirror.DOMAIN + ":Silver_Clean");
        this.gold_icon = reg.registerIcon(MagicMirror.DOMAIN + ":Gold_Clean");
        this.diamond_icon = reg.registerIcon(MagicMirror.DOMAIN + ":Diamond_Clean");

        this.silver_dirty_icon = reg.registerIcon(MagicMirror.DOMAIN + ":Silver_Dirty");
        this.gold_dirty_icon = reg.registerIcon(MagicMirror.DOMAIN + ":Gold_Dirty");
        this.diamond_dirty_icon = reg.registerIcon(MagicMirror.DOMAIN + ":Diamond_Dirty");

        this.glow_icon = reg.registerIcon(MagicMirror.DOMAIN + ":Glow");
        this.shine_icon = reg.registerIcon(MagicMirror.DOMAIN + ":Shine");
        this.blank_icon = reg.registerIcon(MagicMirror.DOMAIN + ":blank");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass)
    {
        if (renderPass == 1)
        {
            return currentMirrorState == 2 || currentMirrorState == 3 ? shine_icon : blank_icon;
        }
        else if (renderPass == 2)
        {
            return currentMirrorState == 1 || currentMirrorState == 3 ? glow_icon : blank_icon;
        }
        return getIconFromDamage(stack.getItemDamage());
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        return getIconFromDamage(stack.getItemDamage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int metadata)
    {
        return 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int i)
    {
        switch (i)
        {
            case 1:
                return gold_icon;
            case 2:
                return diamond_icon;
            case 3:
                return silver_dirty_icon;
            case 4:
                return gold_dirty_icon;
            case 5:
                return diamond_dirty_icon;
        }
        return this.itemIcon;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
        //TODO play charging sound effect
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
        if (!world.isRemote)
        {
            if (canTeleport(player))
            {
                //TODO play charge start sound effect
                if (player.getItemInUse() == null)
                {
                    player.setItemInUse(stack, getMaxItemUseDuration(stack));
                }
            }
            else if (!MirrorHandler.hasLocation(player))
            {
                player.addChatComponentMessage(new ChatComponentTranslation("item.smbmagicmirror:magicMirror.error.noLocation"));
            }
            else if (MirrorHandler.getXpTeleportCost(player) > player.experienceTotal)
            {
                String translation = StatCollector.translateToLocal("item.smbmagicmirror:magicMirror.error.xp");
                translation = translation.replace("%1", "" + (MirrorHandler.getXpTeleportCost(player) - player.experienceTotal));
                translation = translation.replace("%2", "" + MirrorHandler.getXpTeleportCost(player));
                player.addChatComponentMessage(new ChatComponentText(translation));
            }
        }
        return stack;
    }

    /**
     * Can the user teleport
     *
     * @param player
     * @return
     */
    public boolean canTeleport(EntityPlayer player)
    {
        float xp = MirrorHandler.getXpTeleportCost(player);
        return (int) xp > 0 && (!MagicMirror.USE_XP || player.capabilities.isCreativeMode || player.experienceTotal >= xp);
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

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (player.worldObj.provider.hasNoSky)
        {
            sep("\u00a7c", StatCollector.translateToLocal(getUnlocalizedName() + ".error.noSky"), list);
        }
        else
        {
            sep(StatCollector.translateToLocal(getUnlocalizedName() + ".desc"), list);
        }
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

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
        list.add(new ItemStack(item, 1, 4));
        list.add(new ItemStack(item, 1, 5));
    }
}
