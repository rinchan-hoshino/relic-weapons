package dev.rinchan.relicweapons;

import dev.rinchan.relicweapons.registry.RelicWeaponsRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public final class RelicWeapons {
    public static final String MOD_ID = "relic_weapons";
    public static final int VANILLA_GLINT_COLOR = -1;
    public static final int DEFAULT_RADIANCE_LEVEL = 12;

    private RelicWeapons() {
    }

    public static void applyEnchantmentGlow(ItemStack stack, int color) {
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        if (color >= 0) {
            stack.set(RelicWeaponsRegistries.GLINT_COLOR.get(), color & 0xFFFFFF);
        } else {
            stack.remove(RelicWeaponsRegistries.GLINT_COLOR.get());
        }
    }

    public static void applyTextureLight(ItemStack stack, int lightLevel) {
        stack.set(RelicWeaponsRegistries.RADIANCE_LEVEL.get(), Math.max(0, Math.min(15, lightLevel)));
    }

    public static int radianceLevel(ItemStack stack) {
        Integer level = stack.get(RelicWeaponsRegistries.RADIANCE_LEVEL.get());
        return level == null ? 0 : Math.max(0, Math.min(15, level));
    }
}
