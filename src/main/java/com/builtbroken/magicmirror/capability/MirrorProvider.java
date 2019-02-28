package com.builtbroken.magicmirror.capability;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by StrikerRocker on 22/6/18.
 */
public class MirrorProvider implements ICapabilityProvider
{
    private static final LazyOptional<IMirrorData> holder = LazyOptional.of(MirrorData::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side)
    {
        return MirrorStorage.CAPABILITY_MIRROR.orEmpty(cap, holder);
    }
}