package com.builtbroken.magicmirror.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.builtbroken.magicmirror.capability.MirrorStorage.CAPABILITY_MIRROR;

/**
 * Created by StrikerRocker on 22/6/18.
 */
public class MirrorProvider implements ICapabilitySerializable<NBTTagCompound>
{

    IMirrorData instance = CAPABILITY_MIRROR.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CAPABILITY_MIRROR;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return hasCapability(capability, facing) ? CAPABILITY_MIRROR.<T>cast(instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        return (NBTTagCompound) CAPABILITY_MIRROR.getStorage().writeNBT(CAPABILITY_MIRROR, instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        CAPABILITY_MIRROR.getStorage().readNBT(CAPABILITY_MIRROR, instance, null, nbt);
    }
}