package io.github.strikerrocker.magicmirror.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import io.github.strikerrocker.magicmirror.MagicMirror;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = MagicMirror.DOMAIN)
public class Config {
    public static final ForgeConfigSpec SERVER_CONFIG;
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    static {
        ConfigCost.init(SERVER_BUILDER);
        ConfigLoot.init(SERVER_BUILDER);
        ConfigUse.init(SERVER_BUILDER);
        SERVER_CONFIG = SERVER_BUILDER.build();
    }


    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}
