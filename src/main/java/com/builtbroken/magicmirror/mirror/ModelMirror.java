package com.builtbroken.magicmirror.mirror;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static com.builtbroken.magicmirror.MagicMirror.itemMirror;

/**
 * Created by StrikerRocker on 23/6/18.
 */
public class ModelMirror implements ItemMeshDefinition {

    public ModelMirror(Item item){
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        Item item=stack.getItem();
        if (item instanceof ItemMirror){
            byte state = ((ItemMirror) item).currentMirrorState;
            int meta = stack.getMetadata();
            Map<Integer,String> type = new HashMap<>();
            type.put(0,"silver");
            type.put(1,"gold");
            type.put(2,"diamond");
            type.put(3,"silver_dirty");
            type.put(4,"gold_dirty");
            type.put(5,"diamond_dirty");
            String[] metaType= {"silver","gold","diamond","silver_dirty","gold_dirty","diamond_dirty"};
            if (state==2||state==3){
                return new ModelResourceLocation(itemMirror.getRegistryName(),type.get(meta) + "shine");
            }else if (state==1||state==3){
                return new ModelResourceLocation(itemMirror.getRegistryName(),type.get(meta) + "glow");
            }else return new ModelResourceLocation(itemMirror.getRegistryName(),type.get(meta) + "blank");
        }
        return null;
    }
}