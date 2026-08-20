package dev.rinchan.relicweapons.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Serializable snapshot of every independent visual-effect channel owned by Relic Weapons. */
public record VisualEffectProfile(
        boolean glint,
        Optional<Integer> glintColor,
        int radianceLevel,
        Optional<ParticleEffect> particle) {
    public static Codec<VisualEffectProfile> codec() {
        return Serialization.CODEC;
    }

    public static StreamCodec<RegistryFriendlyByteBuf, VisualEffectProfile> streamCodec() {
        return Serialization.STREAM_CODEC;
    }

    public VisualEffectProfile {
        Objects.requireNonNull(glintColor, "glintColor");
        Objects.requireNonNull(particle, "particle");
        glintColor = glintColor.map(color -> color & 0xFFFFFF);
        if (!glint && glintColor.isPresent()) {
            throw new IllegalArgumentException("glint_color requires glint=true");
        }
        if (radianceLevel < 0 || radianceLevel > 15) {
            throw new IllegalArgumentException("radianceLevel must be between 0 and 15");
        }
    }

    public static VisualEffectProfile empty() {
        return new VisualEffectProfile(false, Optional.empty(), 0, Optional.empty());
    }

    public boolean isEmpty() {
        return !glint && radianceLevel == 0 && particle.isEmpty();
    }

    private static final class Serialization {
        private static final Codec<VisualEffectProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("glint", false).forGetter(VisualEffectProfile::glint),
                Codec.INT.optionalFieldOf("glint_color").forGetter(VisualEffectProfile::glintColor),
                Codec.intRange(0, 15).optionalFieldOf("radiance_level", 0).forGetter(VisualEffectProfile::radianceLevel),
                ParticleEffect.CODEC.optionalFieldOf("particle").forGetter(VisualEffectProfile::particle)
            ).apply(instance, VisualEffectProfile::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, VisualEffectProfile> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

        private Serialization() {
        }
    }
}
