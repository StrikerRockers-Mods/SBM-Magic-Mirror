package io.github.strikerrocker.magicmirror.mirror;

public enum MirrorState {
    DEFAULT("_blank"),
    USEABLE("_glow"),
    CHARGED("_shine");

    public final String suffix;

    MirrorState(String suffix) {
        this.suffix = suffix;
    }

    public static MirrorState get(byte state) {
        if (state >= 0 && state < values().length) {
            return values()[state];
        }
        return DEFAULT;
    }
}
