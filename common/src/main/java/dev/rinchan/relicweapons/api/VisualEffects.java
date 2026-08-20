package dev.rinchan.relicweapons.api;

import dev.rinchan.relicweapons.registry.RelicWeaponsRegistries;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

/** Stable programmatic access to Relic Weapons' item-owned visual components. */
public final class VisualEffects {
    private VisualEffects() {
    }

    public static void setGlint(ItemStack stack, int rgb) {
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        stack.set(RelicWeaponsRegistries.GLINT_COLOR, rgb & 0xFFFFFF);
    }

    public static OptionalInt glintColor(ItemStack stack) {
        Integer color = stack.get(RelicWeaponsRegistries.GLINT_COLOR);
        return color == null ? OptionalInt.empty() : OptionalInt.of(color & 0xFFFFFF);
    }

    public static void setRadiance(ItemStack stack, int level) {
        if (level < 0 || level > 15) {
            throw new IllegalArgumentException("radiance level must be in [0, 15]");
        }
        stack.set(RelicWeaponsRegistries.RADIANCE_LEVEL, level);
    }

    public static int radianceLevel(ItemStack stack) {
        Integer level = stack.get(RelicWeaponsRegistries.RADIANCE_LEVEL);
        return level == null ? 0 : level;
    }

    public static void setParticle(ItemStack stack, ParticleEffect effect) {
        stack.set(RelicWeaponsRegistries.PARTICLE_EFFECT, effect);
    }

    public static Optional<ParticleEffect> particle(ItemStack stack) {
        return Optional.ofNullable(stack.get(RelicWeaponsRegistries.PARTICLE_EFFECT));
    }

    public static VisualEffectProfile capture(ItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        Optional<Integer> color = Optional.ofNullable(stack.get(RelicWeaponsRegistries.GLINT_COLOR));
        boolean glint = Boolean.TRUE.equals(stack.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE)) || color.isPresent();
        int radiance = stack.getOrDefault(RelicWeaponsRegistries.RADIANCE_LEVEL, 0);
        Optional<ParticleEffect> particle = Optional.ofNullable(stack.get(RelicWeaponsRegistries.PARTICLE_EFFECT));
        return new VisualEffectProfile(glint, color, radiance, particle);
    }

    public static void apply(ItemStack stack, VisualEffectProfile profile) {
        Objects.requireNonNull(stack, "stack");
        Objects.requireNonNull(profile, "profile");
        clear(stack);
        if (profile.glint()) {
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            profile.glintColor().ifPresent(color -> stack.set(RelicWeaponsRegistries.GLINT_COLOR, color));
        }
        if (profile.radianceLevel() > 0) {
            setRadiance(stack, profile.radianceLevel());
        }
        profile.particle().ifPresent(effect -> setParticle(stack, effect));
    }

    public static void copy(ItemStack source, ItemStack target) {
        apply(target, capture(source));
    }

    public static void clear(ItemStack stack) {
        stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        stack.remove(RelicWeaponsRegistries.GLINT_COLOR);
        stack.remove(RelicWeaponsRegistries.RADIANCE_LEVEL);
        stack.remove(RelicWeaponsRegistries.PARTICLE_EFFECT);
    }

}
