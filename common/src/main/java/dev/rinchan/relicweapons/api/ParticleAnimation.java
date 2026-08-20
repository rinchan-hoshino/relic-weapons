package dev.rinchan.relicweapons.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.util.StringRepresentable;

/** Bounded motion parameters shared by every particle type. */
public record ParticleAnimation(
        Shape shape,
        float rate,
        float spread,
        Direction direction,
        float speed,
        float inheritMotion) {
    public static final float MAX_RATE = 64.0F;
    public static final float MAX_SPREAD = 4.0F;
    public static final float MAX_SPEED = 4.0F;

    public static final MapCodec<ParticleAnimation> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Shape.CODEC.optionalFieldOf("shape", Shape.POINT).forGetter(ParticleAnimation::shape),
            Codec.FLOAT.optionalFieldOf("rate", 4.0F).forGetter(ParticleAnimation::rate),
            Codec.FLOAT.optionalFieldOf("spread", 0.25F).forGetter(ParticleAnimation::spread),
            Direction.CODEC.optionalFieldOf("direction", Direction.RANDOM).forGetter(ParticleAnimation::direction),
            Codec.FLOAT.optionalFieldOf("speed", 0.0F).forGetter(ParticleAnimation::speed),
            Codec.FLOAT.optionalFieldOf("inherit_motion", 0.0F).forGetter(ParticleAnimation::inheritMotion)
        ).apply(instance, ParticleAnimation::new));
    public static final Codec<ParticleAnimation> CODEC = MAP_CODEC.codec();

    public ParticleAnimation {
        Objects.requireNonNull(shape, "shape");
        Objects.requireNonNull(direction, "direction");
        requireRange("rate", rate, 0.0F, MAX_RATE);
        requireRange("spread", spread, 0.0F, MAX_SPREAD);
        requireRange("speed", speed, 0.0F, MAX_SPEED);
        requireRange("inherit_motion", inheritMotion, 0.0F, 1.0F);
    }

    private static void requireRange(String name, float value, float minimum, float maximum) {
        if (!Float.isFinite(value) || value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " must be in [" + minimum + ", " + maximum + "]");
        }
    }

    public enum Shape implements StringRepresentable {
        POINT,
        SPHERE,
        RING,
        TRAIL;

        public static final Codec<Shape> CODEC = StringRepresentable.fromEnum(Shape::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum Direction implements StringRepresentable {
        RANDOM,
        UP,
        OUTWARD,
        FORWARD,
        MOTION;

        public static final Codec<Direction> CODEC = StringRepresentable.fromEnum(Direction::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
