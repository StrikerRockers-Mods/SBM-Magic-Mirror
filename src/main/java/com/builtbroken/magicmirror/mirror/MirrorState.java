package com.builtbroken.magicmirror.mirror;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
public enum MirrorState
{
    DEFAULT("_blank"),
    USEABLE("_glow"),
    CHARGED("_shine"),
    CHARGED_USEABLE("_shine");

    public final String model_suffix;

    MirrorState(String model_suffix)
    {
        this.model_suffix = model_suffix;
    }

    public static MirrorState get(byte state)
    {
        if (state >= 0 && state < values().length)
        {
            return values()[state];
        }
        return DEFAULT;
    }
}
