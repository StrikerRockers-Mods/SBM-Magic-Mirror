package com.builtbroken.magicmirror.capability;

import com.builtbroken.magicmirror.handler.TeleportPos;
import net.minecraft.nbt.INBTBase;
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
    public static Capability<IMirrorData> CAPABILITY_MIRROR = null;

    @Nullable
    @Override
    public INBTBase writeNBT(Capability<IMirrorData> capability, IMirrorData instance, EnumFacing side)
    {
        TeleportPos teleportPos = instance.getLocation();
        final NBTTagCompound tagCompound = new NBTTagCompound();
        if (teleportPos != null) {
            tagCompound.setInt("x", teleportPos.x);
            tagCompound.setInt("y", teleportPos.y);
            tagCompound.setInt("z", teleportPos.z);
            tagCompound.setFloat("yaw", teleportPos.yaw);
            tagCompound.setFloat("pitch", teleportPos.pitch);
        }
        return tagCompound;
    }

    @Override
    public void readNBT(Capability<IMirrorData> capability, IMirrorData instance, EnumFacing side, INBTBase nbt)
    {
        final NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setLocation(new TeleportPos(
                tag.getInt("x"),
                tag.getInt("y"),
                tag.getInt("z"),
                tag.getFloat("yaw"),
                tag.getFloat("pitch")));
    }
}