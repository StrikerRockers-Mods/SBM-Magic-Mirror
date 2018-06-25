package com.builtbroken.magicmirror.network;

import com.builtbroken.magicmirror.mirror.ItemMirror;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Updates data to the client about mirror usage
 */
public class PacketClientUpdate implements IMessage
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
    public void fromBytes(ByteBuf buf)
    {
        xpCost = buf.readInt();
        state = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(xpCost);
        buf.writeByte(state);
    }

    public static class Handler implements IMessageHandler<PacketClientUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketClientUpdate message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                ItemMirror.currentXPCostToTeleport = message.xpCost;
                ItemMirror.currentMirrorState = message.state;
            });
            return null;
        }
    }
}
