package dev.rinchan.relicweapons.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import dev.rinchan.relicweapons.mixin.BufferSourceAccessor;
import dev.rinchan.relicweapons.mixin.OutlineBufferSourceAccessor;
import java.util.SequencedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;

/** Vanilla-equivalent foil composition with fixed, per-vertex-colored glint batches. */
public final class ColoredGlintBuffers {
    private ColoredGlintBuffers() {
    }

    public static VertexConsumer foil(
            MultiBufferSource buffers, RenderType baseType, boolean itemGlint, boolean hasFoil, int rgb) {
        if (!hasFoil) {
            return buffers.getBuffer(baseType);
        }
        RenderType glintType = Minecraft.useShaderTransparency() && baseType == Sheets.translucentItemSheet()
            ? ColoredGlintRenderTypes.itemTranslucent()
            : itemGlint ? ColoredGlintRenderTypes.item() : ColoredGlintRenderTypes.entity();
        return VertexMultiConsumer.create(coloredBuffer(buffers, glintType, rgb), buffers.getBuffer(baseType));
    }

    public static VertexConsumer foilDirect(
            MultiBufferSource buffers, RenderType baseType, boolean itemGlint, boolean hasFoil, int rgb) {
        if (!hasFoil) {
            return buffers.getBuffer(baseType);
        }
        RenderType glintType = itemGlint
            ? ColoredGlintRenderTypes.item()
            : ColoredGlintRenderTypes.entityDirect();
        return VertexMultiConsumer.create(coloredBuffer(buffers, glintType, rgb), buffers.getBuffer(baseType));
    }

    public static VertexConsumer compass(
            MultiBufferSource buffers, RenderType baseType, PoseStack.Pose pose, int rgb) {
        VertexConsumer glint = new SheetedDecalTextureGenerator(
            coloredBuffer(buffers, ColoredGlintRenderTypes.item(), rgb), pose, 0.0078125F);
        return VertexMultiConsumer.create(glint, buffers.getBuffer(baseType));
    }

    public static VertexConsumer armor(MultiBufferSource buffers, int rgb) {
        return coloredBuffer(buffers, ColoredGlintRenderTypes.armor(), rgb);
    }

    private static VertexConsumer coloredBuffer(MultiBufferSource buffers, RenderType type, int rgb) {
        ensureFixedBuffer(buffers, type);
        return new ColoredVertexConsumer(buffers.getBuffer(type), rgb & 0xFFFFFF);
    }

    private static void ensureFixedBuffer(MultiBufferSource buffers, RenderType type) {
        MultiBufferSource.BufferSource source = unwrap(buffers);
        if (source == null) {
            throw new IllegalStateException(
                "Colored glint requires Minecraft's fixed BufferSource, got " + buffers.getClass().getName());
        }
        SequencedMap<RenderType, ByteBufferBuilder> fixed = ((BufferSourceAccessor) source).relicWeapons$fixedBuffers();
        fixed.computeIfAbsent(type, key -> new ByteBufferBuilder(key.bufferSize()));
    }

    private static MultiBufferSource.BufferSource unwrap(MultiBufferSource buffers) {
        if (buffers instanceof MultiBufferSource.BufferSource source) {
            return source;
        }
        if (buffers instanceof OutlineBufferSource outline) {
            return ((OutlineBufferSourceAccessor) outline).relicWeapons$bufferSource();
        }
        return null;
    }
}
