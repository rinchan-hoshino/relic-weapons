package dev.rinchan.relicweapons.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.rinchan.relicweapons.RelicWeapons;
import dev.rinchan.relicweapons.api.ParticleAnimation;
import dev.rinchan.relicweapons.api.ParticleEffect;
import dev.rinchan.relicweapons.api.VisualEffects;
import dev.rinchan.relicweapons.registry.RelicWeaponsRegistries;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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

public record GlowSmithingRecipe(
        Ingredient template,
        Ingredient addition,
        GlowMode mode,
        int color,
        int lightLevel,
        Optional<ParticleEffect> particle) implements SmithingRecipe {
    private static final ParticleAnimation DEFAULT_PARTICLE_ANIMATION = new ParticleAnimation(
        ParticleAnimation.Shape.POINT,
        4.0F,
        0.25F,
        ParticleAnimation.Direction.RANDOM,
        0.0F,
        0.0F);

    public static final MapCodec<EffectSettings> EFFECT_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        GlowMode.CODEC.optionalFieldOf("glow_type", GlowMode.ENCHANTMENT).forGetter(EffectSettings::mode),
        Codec.INT.optionalFieldOf("color", RelicWeapons.VANILLA_GLINT_COLOR).forGetter(EffectSettings::color),
        Codec.INT.optionalFieldOf("light_level", RelicWeapons.DEFAULT_RADIANCE_LEVEL).forGetter(EffectSettings::lightLevel),
        ParticleTypes.CODEC.optionalFieldOf("particle").forGetter(settings -> settings.particle().map(ParticleEffect::particle)),
        ParticleAnimation.MAP_CODEC.forGetter(settings -> settings.particle().map(ParticleEffect::animation).orElse(DEFAULT_PARTICLE_ANIMATION))
    ).apply(instance, EffectSettings::create));

    public static final MapCodec<GlowSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("template").forGetter(GlowSmithingRecipe::template),
        Ingredient.CODEC.optionalFieldOf("addition", Ingredient.EMPTY).forGetter(GlowSmithingRecipe::addition),
        EFFECT_CODEC.forGetter(GlowSmithingRecipe::settings)
    ).apply(instance, (template, addition, settings) -> new GlowSmithingRecipe(
            template, addition, settings.mode(), settings.color(), settings.lightLevel(), settings.particle())));

    public GlowSmithingRecipe {
        particle = particle == null ? Optional.empty() : particle;
        if (mode == GlowMode.PARTICLE && particle.isEmpty()) {
            throw new IllegalArgumentException("particle glow recipes require a particle specification");
        }
    }

    private EffectSettings settings() {
        return new EffectSettings(mode, color, lightLevel, particle);
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return template.test(input.template()) && matchesAddition(input.addition()) && !input.base().isEmpty();
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack result = input.base().copyWithCount(1);
        apply(result);
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack stack = new ItemStack(Items.IRON_SWORD);
        apply(stack);
        return stack;
    }

    private void apply(ItemStack stack) {
        switch (mode) {
            case ENCHANTMENT -> VisualEffects.setGlint(stack, color);
            case TEXTURE_LIGHT -> VisualEffects.setRadiance(stack, lightLevel);
            case PARTICLE -> VisualEffects.setParticle(stack, particle.orElseThrow());
        }
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return template.test(stack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return !stack.isEmpty();
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return matchesAddition(stack);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RelicWeaponsRegistries.GLOW_SMITHING;
    }

    @Override
    public boolean isIncomplete() {
        return template.getItems().length == 0 || (!addition.isEmpty() && addition.getItems().length == 0);
    }

    private boolean matchesAddition(ItemStack stack) {
        return addition.isEmpty() ? stack.isEmpty() : addition.test(stack);
    }

    public record EffectSettings(GlowMode mode, int color, int lightLevel, Optional<ParticleEffect> particle) {
        private static EffectSettings create(
                GlowMode mode,
                int color,
                int lightLevel,
                Optional<ParticleOptions> options,
                ParticleAnimation animation) {
            return new EffectSettings(mode, color, lightLevel, options.map(value -> new ParticleEffect(value, animation)));
        }
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
                Optional<ParticleEffect> particle = ByteBufCodecs.optional(ParticleEffect.STREAM_CODEC).decode(buffer);
                return new GlowSmithingRecipe(template, addition, mode, color, lightLevel, particle);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, GlowSmithingRecipe recipe) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.template);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.addition);
                ByteBufCodecs.STRING_UTF8.encode(buffer, recipe.mode.id());
                ByteBufCodecs.VAR_INT.encode(buffer, recipe.color);
                ByteBufCodecs.VAR_INT.encode(buffer, recipe.lightLevel);
                ByteBufCodecs.optional(ParticleEffect.STREAM_CODEC).encode(buffer, recipe.particle);
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
