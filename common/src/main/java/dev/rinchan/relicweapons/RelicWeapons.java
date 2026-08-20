package dev.rinchan.relicweapons;

import dev.rinchan.relicweapons.api.VisualEffects;
import dev.rinchan.relicweapons.registry.RelicWeaponsRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public final class RelicWeapons {
    public static final String MOD_ID = "relic_weapons";
    public static final int VANILLA_GLINT_COLOR = -1;
    public static final int DEFAULT_RADIANCE_LEVEL = 2;

    private RelicWeapons() {
    }

    public static void applyEnchantmentGlow(ItemStack stack, int color) {
        if (color >= 0) {
            VisualEffects.setGlint(stack, color);
        } else {
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            stack.remove(RelicWeaponsRegistries.GLINT_COLOR);
        }
    }

    public static void applyTextureLight(ItemStack stack, int lightLevel) {
        VisualEffects.setRadiance(stack, Math.max(0, Math.min(15, lightLevel)));
    }

    public static int radianceLevel(ItemStack stack) {
        return Math.max(0, Math.min(15, VisualEffects.radianceLevel(stack)));
    }
}
