package com.builtbroken.magicmirror.capability;

import com.builtbroken.magicmirror.handler.TeleportPos;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

/**
 * Created by StrikerRocker on 22/6/18.
 */
public class MirrorStorage implements Capability.IStorage<IMirrorData>
{
    @CapabilityInject(IMirrorData.class)
    public static final Capability<IMirrorData> CAPABILITY_MIRROR = null;

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IMirrorData> capability, IMirrorData instance, EnumFacing side)
    {
        TeleportPos teleportPos = instance.getLocation();
        final NBTTagCompound tagCompound = new NBTTagCompound();
        if (teleportPos != null)
        {
            tagCompound.setInteger("x", teleportPos.x);
            tagCompound.setInteger("y", teleportPos.y);
            tagCompound.setInteger("z", teleportPos.z);
            tagCompound.setFloat("yaw", teleportPos.yaw);
            tagCompound.setFloat("pitch", teleportPos.pitch);
        }
        return tagCompound;
    }

    @Override
    public void readNBT(Capability<IMirrorData> capability, IMirrorData instance, EnumFacing side, NBTBase nbt)
    {
        final NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setLocation(new TeleportPos(
                tag.getInteger("x"),
                tag.getInteger("y"),
                tag.getInteger("z"),
                tag.getFloat("yaw"),
                tag.getFloat("pitch")));
    }
}