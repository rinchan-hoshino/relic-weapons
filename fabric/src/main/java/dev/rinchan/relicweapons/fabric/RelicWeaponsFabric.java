package dev.rinchan.relicweapons.fabric;

import dev.rinchan.relicweapons.RelicWeapons;
import dev.rinchan.relicweapons.registry.RelicWeaponsRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class RelicWeaponsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("glint_color"), RelicWeaponsRegistries.GLINT_COLOR);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("radiance_level"), RelicWeaponsRegistries.RADIANCE_LEVEL);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("particle_effect"), RelicWeaponsRegistries.PARTICLE_EFFECT);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id("glow_smithing"), RelicWeaponsRegistries.GLOW_SMITHING);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(RelicWeapons.MOD_ID, path);
    }
}
