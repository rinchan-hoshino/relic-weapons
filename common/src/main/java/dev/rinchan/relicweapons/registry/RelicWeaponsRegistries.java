package dev.rinchan.relicweapons.registry;

import com.mojang.serialization.Codec;
import dev.rinchan.relicweapons.api.ParticleEffect;
import dev.rinchan.relicweapons.recipe.GlowSmithingRecipe;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;

/** Loader-neutral component and serializer instances registered by each loader entrypoint. */
public final class RelicWeaponsRegistries {
    public static final DataComponentType<Integer> GLINT_COLOR = DataComponentType.<Integer>builder()
        .persistent(Codec.INT)
        .networkSynchronized(ByteBufCodecs.VAR_INT)
        .build();
    public static final DataComponentType<Integer> RADIANCE_LEVEL = DataComponentType.<Integer>builder()
        .persistent(Codec.INT)
        .networkSynchronized(ByteBufCodecs.VAR_INT)
        .build();
    public static final DataComponentType<ParticleEffect> PARTICLE_EFFECT = DataComponentType.<ParticleEffect>builder()
        .persistent(ParticleEffect.CODEC)
        .networkSynchronized(ParticleEffect.STREAM_CODEC)
        .build();
    public static final RecipeSerializer<GlowSmithingRecipe> GLOW_SMITHING = new GlowSmithingRecipe.Serializer();

    private RelicWeaponsRegistries() {
    }
}
