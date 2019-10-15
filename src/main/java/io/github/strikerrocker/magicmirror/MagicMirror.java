package io.github.strikerrocker.magicmirror;

import io.github.strikerrocker.magicmirror.capability.IMirrorData;
import io.github.strikerrocker.magicmirror.capability.MirrorData;
import io.github.strikerrocker.magicmirror.config.Config;
import io.github.strikerrocker.magicmirror.handler.TeleportPos;
import io.github.strikerrocker.magicmirror.mirror.MirrorItem;
import io.github.strikerrocker.magicmirror.mirror.MirrorSubType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(value = MagicMirror.DOMAIN)
public class MagicMirror {
    //TODO make the mirror glow when it can be used
    //TODO make it have a visual change when it has a stored location
    //TODO show some kind of progress bar for XP needed
    //TODO Event to fire(Marks position, etc)

    /**
     * Are we running in developer mode, used to enabled additional debug and tools
     */
    public static final boolean runningAsDev = false;
    /**
     * Information output thing
     */
    public static final Logger logger = LogManager.getLogger("SBM-MagicMirror");
    public static final String DOMAIN = "sbmmagicmirror";
    public static List<MirrorItem> mirrors = new ArrayList<>();
    /**
     * Mirror item used to activate and tick mirror handler
     */
    public static MirrorItem silverMirror;
    @CapabilityInject(IMirrorData.class)
    public static Capability<IMirrorData> CAPABILITY_MIRROR = null;

    public MagicMirror() {
        Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("magic_mirror.toml"));
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IMirrorData.class, new Capability.IStorage<IMirrorData>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IMirrorData> capability, IMirrorData instance, Direction side) {
                TeleportPos teleportPos = instance.getLocation();
                CompoundNBT tagCompound = new CompoundNBT();
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
            public void readNBT(Capability<IMirrorData> capability, IMirrorData instance, Direction side, INBT nbt) {
                final CompoundNBT tag = (CompoundNBT) nbt;
                instance.setLocation(new TeleportPos(
                        tag.getInt("x"),
                        tag.getInt("y"),
                        tag.getInt("z"),
                        tag.getFloat("yaw"),
                        tag.getFloat("pitch")));
            }
        }, MirrorData::new);
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> registry) {
        for (MirrorSubType type : MirrorSubType.values()) {
            MirrorItem item = new MirrorItem(type);
            mirrors.add(item);
            registry.getRegistry().register(item);
            if (type == MirrorSubType.SILVER_DIRTY) {
                silverMirror = item;
            }
        }
    }
}