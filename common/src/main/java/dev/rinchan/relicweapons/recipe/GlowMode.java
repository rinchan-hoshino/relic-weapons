package dev.rinchan.relicweapons.recipe;

import com.mojang.serialization.Codec;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum GlowMode implements StringRepresentable {
    ENCHANTMENT,
    TEXTURE_LIGHT,
    PARTICLE;

    public static final Codec<GlowMode> CODEC = StringRepresentable.fromEnum(GlowMode::values);

    public String id() {
        return getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static GlowMode fromId(String id) {
        for (GlowMode mode : values()) {
            if (mode.id().equals(id)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown glow type " + id);
    }
}
