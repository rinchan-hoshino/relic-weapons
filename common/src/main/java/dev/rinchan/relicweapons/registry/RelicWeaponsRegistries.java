package dev.rinchan.relicweapons.registry;

import com.mojang.serialization.Codec;
import dev.rinchan.relicweapons.RelicWeapons;
import dev.rinchan.relicweapons.recipe.GlowSmithingRecipe;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RelicWeaponsRegistries {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, RelicWeapons.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, RelicWeapons.MOD_ID);
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, RelicWeapons.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> GLINT_COLOR = DATA_COMPONENTS.register(
        "glint_color",
        () -> DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .build()
    );
    public static final Supplier<DataComponentType<Integer>> RADIANCE_LEVEL = DATA_COMPONENTS.register(
        "radiance_level",
        () -> DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .build()
    );

    public static final Supplier<Item> ENCHANTMENT_GLOW = ITEMS.register(
        "enchantment_glow",
        () -> new Item(new Item.Properties()
            .stacksTo(16)
            .rarity(Rarity.UNCOMMON)
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true))
    );
    public static final Supplier<Item> TEXTURE_LIGHT = ITEMS.register(
        "texture_light",
        () -> new Item(new Item.Properties()
            .stacksTo(16)
            .rarity(Rarity.UNCOMMON))
    );

    public static final Supplier<RecipeSerializer<GlowSmithingRecipe>> GLOW_SMITHING = RECIPE_SERIALIZERS.register(
        "glow_smithing",
        GlowSmithingRecipe.Serializer::new
    );

    private RelicWeaponsRegistries() {
    }

    public static void register(IEventBus modBus) {
        DATA_COMPONENTS.register(modBus);
        ITEMS.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }
}
