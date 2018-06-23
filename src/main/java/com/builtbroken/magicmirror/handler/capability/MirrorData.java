package com.builtbroken.magicmirror.handler.capability;

import com.builtbroken.magicmirror.handler.TeleportPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

/**
 * Created by StrikerRocker on 22/6/18.
 */
public class MirrorData implements IMirrorData{

    private TeleportPos teleportPos;
    private int x;
    private int y;
    private int z;
    private float yaw;
    private float pitch;

    @Override
    public boolean hasLocation()
    {
        return teleportPos != null;
    }

    @Override
    public TeleportPos getLocation()
    {
        return teleportPos;
    }

    @Override
    public void setLocation(TeleportPos potentialTP)
    {
        if (potentialTP !=null){
            teleportPos = potentialTP;
            x = teleportPos.x;
            y = teleportPos.y;
            z = teleportPos.z;
            yaw = teleportPos.yaw;
            pitch = teleportPos.pitch;
        }
    }

    @Override
    public float getXpTeleportCost(EntityPlayer player)
    {
        if (hasLocation()){
            return getLocation().getTeleportCost(player);
        }
        return 0;
    }

    @Override
    public void setLocation(EntityPlayer player, TeleportPos potentialTP)
    {
        setLocation(potentialTP);
            String translation =  I18n.translateToLocal("item.smbmagicmirror:magicmirror.location.set").replace("%1", "" + potentialTP.x).replace("%2", "" + potentialTP.y).replace("%3", "" + potentialTP.z);
            if(translation != null)
            {
                player.sendStatusMessage(new TextComponentString(translation),true);
            }
    }
}