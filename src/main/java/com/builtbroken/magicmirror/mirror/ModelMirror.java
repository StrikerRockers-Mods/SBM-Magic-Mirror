package com.builtbroken.magicmirror.mirror;

import com.builtbroken.magicmirror.MagicMirror;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by StrikerRocker on 23/6/18.
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN, value = Side.CLIENT)
public class ModelMirror implements ItemMeshDefinition
{
    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        return MirrorSubType.get(stack.getMetadata()).getStateResourceLocation(ItemMirror.currentMirrorState);
    }

    @SubscribeEvent
    public static void registerItem(ModelRegistryEvent event)
    {
        ModelLoader.setCustomMeshDefinition(MagicMirror.itemMirror, new ModelMirror());
        for (MirrorSubType subType : MirrorSubType.values())
        {
            for (MirrorState state : MirrorState.values())
            {
                ModelBakery.registerItemVariants(MagicMirror.itemMirror, subType.getStateResourceLocation(state));
            }
        }
    }
}