package com.builtbroken.magicmirror.network;

import com.builtbroken.magicmirror.mirror.ItemMirror;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Updates data to the client about mirror usage
 */
public class PacketClientUpdate extends Packet
{
    int xpCost = 0;
    byte state = 0;


    public PacketClientUpdate()
    {
    }

    public PacketClientUpdate(int xpCost, boolean isCharged, boolean isActive)
    {
        this.xpCost = xpCost;
        this.state = isCharged && isActive ? (byte) 3 : isCharged ? (byte) 2 : isActive ? (byte) 1 : 0;
    }

    @Override
    public void write(ByteBuf buffer)
    {
        buffer.writeInt(xpCost);
        buffer.writeByte(state);
    }

    @Override
    public void read(ByteBuf buffer)
    {
        xpCost = buffer.readInt();
        state = buffer.readByte();
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
        ItemMirror.currentXPCostToTeleport = xpCost;
        ItemMirror.currentMirrorState = state;
    }
}
