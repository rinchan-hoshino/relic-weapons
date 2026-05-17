package dev.rinchan.luminousgear;

import dev.rinchan.luminousgear.registry.LuminousGearRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public final class LuminousGear {
    public static final String MOD_ID = "luminous_gear";
    public static final int VANILLA_GLINT_COLOR = -1;
    public static final int DEFAULT_RADIANCE_LEVEL = 2;

    private LuminousGear() {
    }

    public static void applyEnchantmentGlow(ItemStack stack, int color) {
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        if (color >= 0) {
            stack.set(LuminousGearRegistries.GLINT_COLOR.get(), color & 0xFFFFFF);
        } else {
            stack.remove(LuminousGearRegistries.GLINT_COLOR.get());
        }
    }

    public static void applyTextureLight(ItemStack stack, int lightLevel) {
        stack.set(LuminousGearRegistries.RADIANCE_LEVEL.get(), Math.max(0, Math.min(15, lightLevel)));
    }

    public static int radianceLevel(ItemStack stack) {
        Integer level = stack.get(LuminousGearRegistries.RADIANCE_LEVEL.get());
        return level == null ? 0 : Math.max(0, Math.min(15, level));
    }
}
