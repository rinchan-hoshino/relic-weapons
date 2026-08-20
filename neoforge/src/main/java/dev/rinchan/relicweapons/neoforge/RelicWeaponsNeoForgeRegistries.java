package dev.rinchan.relicweapons.neoforge;

import dev.rinchan.relicweapons.RelicWeapons;
import dev.rinchan.relicweapons.registry.RelicWeaponsRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

final class RelicWeaponsNeoForgeRegistries {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, RelicWeapons.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, RelicWeapons.MOD_ID);

    static {
        DATA_COMPONENTS.register("glint_color", () -> RelicWeaponsRegistries.GLINT_COLOR);
        DATA_COMPONENTS.register("radiance_level", () -> RelicWeaponsRegistries.RADIANCE_LEVEL);
        DATA_COMPONENTS.register("particle_effect", () -> RelicWeaponsRegistries.PARTICLE_EFFECT);
        RECIPE_SERIALIZERS.register("glow_smithing", () -> RelicWeaponsRegistries.GLOW_SMITHING);
    }

    private RelicWeaponsNeoForgeRegistries() {
    }

    static void register(IEventBus modBus) {
        DATA_COMPONENTS.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }
}
