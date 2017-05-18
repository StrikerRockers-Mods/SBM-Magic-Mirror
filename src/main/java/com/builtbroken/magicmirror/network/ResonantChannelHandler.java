package com.builtbroken.magicmirror.network;

import com.builtbroken.magicmirror.MagicMirror;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author tgame14
 * @since 31/05/14
 */
public class ResonantChannelHandler extends FMLIndexedMessageToMessageCodec<Packet>
{
    public boolean silenceStackTrace = false; //TODO add command and config

    public ResonantChannelHandler()
    {
        this.addDiscriminator(0, PacketClientUpdate.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, Packet packet, ByteBuf target) throws Exception
    {
        try
        {
            packet.write(target);
        }
        catch (Exception e)
        {
            if (!silenceStackTrace)
                MagicMirror.logger.error("Failed to encode packet " + packet, e);
            else
                MagicMirror.logger.error("Failed to encode packet " + packet + " E: " + e.getMessage());
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, Packet packet)
    {
        try
        {
            packet.read(source);
        }
        catch (Exception e)
        {
            if (!silenceStackTrace)
                MagicMirror.logger.error("Failed to decode packet " + packet, e);
            else
                MagicMirror.logger.error("Failed to decode packet " + packet + " E: " + e.getMessage());
        }
    }
}
