package dev.rinchan.relicweapons.client;

import net.minecraft.client.renderer.ShaderInstance;

/** Loader-neutral holder populated by the loader's core-shader registration callback. */
public final class RelicShaders {
    private static ShaderInstance coloredGlint;

    private RelicShaders() {
    }

    public static void setColoredGlint(ShaderInstance shader) {
        coloredGlint = shader;
    }

    static ShaderInstance coloredGlint() {
        return coloredGlint;
    }
}
