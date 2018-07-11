package com.builtbroken.magicmirror.mirror;

import com.builtbroken.magicmirror.MagicMirror;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

/**
 * Enum of sub types
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
public enum MirrorSubType
{
    SILVER,
    GOLD,
    DIAMOND,
    SILVER_DIRTY,
    GOLD_DIRTY,
    DIAMOND_DIRTY;

    private final ModelResourceLocation[] stateResourceLocation;

    MirrorSubType()
    {
        stateResourceLocation = new ModelResourceLocation[MirrorState.values().length];
        for (int i = 0; i < MirrorState.values().length; i++)
        {
            final String name = name().toLowerCase() + MirrorState.values()[i].model_suffix;
            stateResourceLocation[i] =
                    new ModelResourceLocation(
                            new ResourceLocation(MagicMirror.DOMAIN, name),
                            "inventory"
                    );
        }
    }

    public static MirrorSubType get(int meta)
    {
        if (meta >= 0 && meta < values().length)
        {
            return values()[meta];
        }
        return SILVER;
    }

    public ModelResourceLocation getStateResourceLocation(MirrorState currentMirrorState)
    {
        return stateResourceLocation[currentMirrorState.ordinal()];
    }
}
