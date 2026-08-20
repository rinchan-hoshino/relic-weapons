package dev.rinchan.relicweapons.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

class ParticleAnimationTest {
    @Test
    void codecRoundTripsEveryAnimationParameter() {
        var animation = new ParticleAnimation(
                ParticleAnimation.Shape.TRAIL,
                12.5F,
                0.75F,
                ParticleAnimation.Direction.FORWARD,
                0.18F,
                0.4F);
        var encoded = ParticleAnimation.CODEC.encodeStart(JsonOps.INSTANCE, animation).getOrThrow();
        assertEquals(animation, ParticleAnimation.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow());
    }

    @Test
    void rejectsParametersThatCouldCreateUnboundedClientWork() {
        assertThrows(IllegalArgumentException.class, () -> animation(65.0F, 0.0F, 0.0F, 0.0F));
        assertThrows(IllegalArgumentException.class, () -> animation(1.0F, 4.1F, 0.0F, 0.0F));
        assertThrows(IllegalArgumentException.class, () -> animation(1.0F, 0.0F, 4.1F, 0.0F));
        assertThrows(IllegalArgumentException.class, () -> animation(1.0F, 0.0F, 0.0F, 1.1F));
    }

    private static ParticleAnimation animation(float rate, float spread, float speed, float inheritMotion) {
        return new ParticleAnimation(
                ParticleAnimation.Shape.POINT,
                rate,
                spread,
                ParticleAnimation.Direction.UP,
                speed,
                inheritMotion);
    }
}
