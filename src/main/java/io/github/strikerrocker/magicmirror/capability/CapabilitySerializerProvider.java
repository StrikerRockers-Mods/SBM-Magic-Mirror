package io.github.strikerrocker.magicmirror.capability;

import io.github.strikerrocker.magicmirror.MagicMirror;
import io.github.strikerrocker.magicmirror.handler.TeleportPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilitySerializerProvider implements ICapabilitySerializable<CompoundTag> {

    private static final LazyOptional<IMirrorData> holder = LazyOptional.of(MirrorData::new);
    IMirrorData mirrorData = new MirrorData();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return MagicMirror.CAPABILITY_MIRROR.orEmpty(cap, holder);
    }

    @Override
    public CompoundTag serializeNBT() {
        TeleportPos teleportPos = mirrorData.getLocation();
        CompoundTag tagCompound = new CompoundTag();
        if (teleportPos != null) {
            tagCompound.putInt("x", teleportPos.x);
            tagCompound.putInt("y", teleportPos.y);
            tagCompound.putInt("z", teleportPos.z);
            tagCompound.putFloat("yaw", teleportPos.yaw);
            tagCompound.putFloat("pitch", teleportPos.pitch);
        }
        return tagCompound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        mirrorData.setLocation(new TeleportPos(
                nbt.getInt("x"),
                nbt.getInt("y"),
                nbt.getInt("z"),
                nbt.getFloat("yaw"),
                nbt.getFloat("pitch")));
    }
}
