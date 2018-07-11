package com.builtbroken.magicmirror;

import com.builtbroken.magicmirror.mirror.ModelMirror;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/8/2016.
 */
@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN)
public class ClientProxy extends CommonProxy
{
    @SubscribeEvent
    public static void registerItem(ModelRegistryEvent event)
    {
        ModelLoader.setCustomMeshDefinition(MagicMirror.itemMirror, new ModelMirror());
        ResourceLocation[] resLocs = new ResourceLocation[6];
        String[] types = {"_blank", "_shine", "_glow"};
        for (String type : types)
        {
            resLocs[0] = new ModelResourceLocation(new ResourceLocation(MagicMirror.DOMAIN, "silver" + type), "inventory");
            resLocs[1] = new ModelResourceLocation(new ResourceLocation(MagicMirror.DOMAIN, "gold" + type), "inventory");
            resLocs[2] = new ModelResourceLocation(new ResourceLocation(MagicMirror.DOMAIN, "diamond" + type), "inventory");
            resLocs[3] = new ModelResourceLocation(new ResourceLocation(MagicMirror.DOMAIN, "silver_dirty" + type), "inventory");
            resLocs[5] = new ModelResourceLocation(new ResourceLocation(MagicMirror.DOMAIN, "diamond_dirty" + type), "inventory");
            resLocs[4] = new ModelResourceLocation(new ResourceLocation(MagicMirror.DOMAIN, "gold_dirty" + type), "inventory");
        }
        ModelBakery.registerItemVariants(MagicMirror.itemMirror, resLocs);
    }
}
