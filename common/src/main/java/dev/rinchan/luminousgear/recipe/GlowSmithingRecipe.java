package dev.rinchan.luminousgear.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.rinchan.luminousgear.LuminousGear;
import dev.rinchan.luminousgear.registry.LuminousGearRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public record GlowSmithingRecipe(Ingredient template, Ingredient addition, GlowMode mode, int color, int lightLevel) implements SmithingRecipe {
    public static final MapCodec<GlowSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("template").forGetter(GlowSmithingRecipe::template),
        Ingredient.CODEC.optionalFieldOf("addition", Ingredient.EMPTY).forGetter(GlowSmithingRecipe::addition),
        Codec.STRING.optionalFieldOf("glow_type", GlowMode.ENCHANTMENT.id()).xmap(GlowMode::fromId, GlowMode::id).forGetter(GlowSmithingRecipe::mode),
        Codec.INT.optionalFieldOf("color", LuminousGear.VANILLA_GLINT_COLOR).forGetter(GlowSmithingRecipe::color),
        Codec.INT.optionalFieldOf("light_level", LuminousGear.DEFAULT_RADIANCE_LEVEL).forGetter(GlowSmithingRecipe::lightLevel)
    ).apply(instance, GlowSmithingRecipe::new));

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return template.test(input.template()) && matchesAddition(input.addition()) && isValidBase(input.base());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack result = input.base().copyWithCount(1);
        if (mode == GlowMode.TEXTURE_LIGHT) {
            LuminousGear.applyTextureLight(result, lightLevel);
        } else {
            LuminousGear.applyEnchantmentGlow(result, color);
        }
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack stack = new ItemStack(Items.IRON_SWORD);
        if (mode == GlowMode.TEXTURE_LIGHT) {
            LuminousGear.applyTextureLight(stack, lightLevel);
        } else {
            LuminousGear.applyEnchantmentGlow(stack, color);
        }
        return stack;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return template.test(stack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return isValidBase(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return matchesAddition(stack);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LuminousGearRegistries.GLOW_SMITHING.get();
    }

    @Override
    public boolean isIncomplete() {
        return template.hasNoItems() || (!addition.isEmpty() && addition.hasNoItems());
    }

    private boolean matchesAddition(ItemStack stack) {
        return addition.isEmpty() ? stack.isEmpty() : addition.test(stack);
    }

    private boolean isValidBase(ItemStack stack) {
        return !stack.isEmpty()
            && !stack.is(LuminousGearRegistries.ENCHANTMENT_GLOW.get())
            && !stack.is(LuminousGearRegistries.TEXTURE_LIGHT.get());
    }

    public static final class Serializer implements RecipeSerializer<GlowSmithingRecipe> {
        public static final StreamCodec<RegistryFriendlyByteBuf, GlowSmithingRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public GlowSmithingRecipe decode(RegistryFriendlyByteBuf buffer) {
                Ingredient template = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
                Ingredient addition = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
                GlowMode mode = GlowMode.fromId(ByteBufCodecs.STRING_UTF8.decode(buffer));
                int color = ByteBufCodecs.VAR_INT.decode(buffer);
                int lightLevel = ByteBufCodecs.VAR_INT.decode(buffer);
                return new GlowSmithingRecipe(template, addition, mode, color, lightLevel);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, GlowSmithingRecipe recipe) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.template);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.addition);
                ByteBufCodecs.STRING_UTF8.encode(buffer, recipe.mode.id());
                ByteBufCodecs.VAR_INT.encode(buffer, recipe.color);
                ByteBufCodecs.VAR_INT.encode(buffer, recipe.lightLevel);
            }
        };

        @Override
        public MapCodec<GlowSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GlowSmithingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
