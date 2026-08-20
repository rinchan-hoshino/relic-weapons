package dev.rinchan.relicweapons.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class VisualEffectProfileTest {
    @Test
    void normalizes_rgb_and_preserves_independent_effect_channels() {
        VisualEffectProfile profile = new VisualEffectProfile(true, Optional.of(0x12ABCDEF), 7, Optional.empty());

        assertTrue(profile.glint());
        assertEquals(0xABCDEF, profile.glintColor().orElseThrow());
        assertEquals(7, profile.radianceLevel());
        assertTrue(profile.particle().isEmpty());
    }

    @Test
    void rejects_color_without_glint_and_invalid_radiance() {
        assertThrows(IllegalArgumentException.class,
            () -> new VisualEffectProfile(false, Optional.of(0xFFFFFF), 0, Optional.empty()));
        assertThrows(IllegalArgumentException.class,
            () -> new VisualEffectProfile(false, Optional.empty(), 16, Optional.empty()));
    }
}
