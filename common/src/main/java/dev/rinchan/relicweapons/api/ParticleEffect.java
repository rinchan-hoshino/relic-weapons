package dev.rinchan.relicweapons.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** A serializable particle type plus bounded motion parameters attached to an item stack. */
public record ParticleEffect(ParticleOptions particle, ParticleAnimation animation) {
    public static final Codec<ParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(ParticleEffect::particle),
            ParticleAnimation.MAP_CODEC.forGetter(ParticleEffect::animation)
        ).apply(instance, ParticleEffect::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleEffect> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public ParticleEffect {
        Objects.requireNonNull(particle, "particle");
        Objects.requireNonNull(animation, "animation");
    }

    public ParticleEffect(
            ParticleOptions particle,
            ParticleAnimation.Shape shape,
            float rate,
            float spread,
            ParticleAnimation.Direction direction,
            float speed,
            float inheritMotion) {
        this(particle, new ParticleAnimation(shape, rate, spread, direction, speed, inheritMotion));
    }

    public ParticleAnimation.Shape shape() {
        return animation.shape();
    }

    public float rate() {
        return animation.rate();
    }

    public float spread() {
        return animation.spread();
    }

    public ParticleAnimation.Direction direction() {
        return animation.direction();
    }

    public float speed() {
        return animation.speed();
    }

    public float inheritMotion() {
        return animation.inheritMotion();
    }
}
