package dev.rinchan.relicweapons.fabric.mixin;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import dev.rinchan.relicweapons.client.ColoredGlintRenderTypes;
import java.util.SequencedMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Fabric counterpart to NeoForge's RegisterRenderBuffersEvent. */
@Mixin(RenderBuffers.class)
public abstract class FabricRenderBuffersMixin {
    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource;immediateWithBuffers(Ljava/util/SequencedMap;Lcom/mojang/blaze3d/vertex/ByteBufferBuilder;)Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;"
        ),
        index = 0
    )
    private SequencedMap<RenderType, ByteBufferBuilder> relicWeapons$registerColoredGlintBuffers(
            SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers) {
        ColoredGlintRenderTypes.all().forEach(
            type -> fixedBuffers.put(type, new ByteBufferBuilder(type.bufferSize())));
        return fixedBuffers;
    }
}
