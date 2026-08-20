package dev.rinchan.relicweapons.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.rinchan.relicweapons.RelicWeapons;
import dev.rinchan.relicweapons.api.VisualEffects;
import dev.rinchan.relicweapons.client.ColoredGlintBuffers;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    private static final ThreadLocal<Deque<Integer>> RELIC_WEAPONS_GLINT_COLORS =
        ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<Deque<Integer>> RELIC_WEAPONS_RADIANCE_LEVELS =
        ThreadLocal.withInitial(ArrayDeque::new);

    @Inject(method = "render", at = @At("HEAD"))
    private void relicWeapons$pushGlintColor(
        ItemStack stack,
        ItemDisplayContext displayContext,
        boolean leftHand,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int combinedLight,
        int combinedOverlay,
        BakedModel model,
        CallbackInfo ci
    ) {
        RELIC_WEAPONS_GLINT_COLORS.get().push(VisualEffects.glintColor(stack).orElse(-1));
        RELIC_WEAPONS_RADIANCE_LEVELS.get().push(RelicWeapons.radianceLevel(stack));
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void relicWeapons$popGlintColor(
        ItemStack stack,
        ItemDisplayContext displayContext,
        boolean leftHand,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int combinedLight,
        int combinedOverlay,
        BakedModel model,
        CallbackInfo ci
    ) {
        Deque<Integer> colors = RELIC_WEAPONS_GLINT_COLORS.get();
        colors.pop();
        if (colors.isEmpty()) {
            RELIC_WEAPONS_GLINT_COLORS.remove();
        }
        Deque<Integer> radiance = RELIC_WEAPONS_RADIANCE_LEVELS.get();
        radiance.pop();
        if (radiance.isEmpty()) {
            RELIC_WEAPONS_RADIANCE_LEVELS.remove();
        }
    }

    @Redirect(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;")
    )
    private VertexConsumer relicWeapons$coloredFoil(
        MultiBufferSource buffers, RenderType baseType, boolean itemGlint, boolean hasFoil
    ) {
        int color = relicWeapons$currentGlintColor();
        return color < 0
            ? ItemRenderer.getFoilBuffer(buffers, baseType, itemGlint, hasFoil)
            : ColoredGlintBuffers.foil(buffers, baseType, itemGlint, hasFoil, color);
    }

    @Redirect(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;")
    )
    private VertexConsumer relicWeapons$coloredFoilDirect(
        MultiBufferSource buffers, RenderType baseType, boolean itemGlint, boolean hasFoil
    ) {
        int color = relicWeapons$currentGlintColor();
        return color < 0
            ? ItemRenderer.getFoilBufferDirect(buffers, baseType, itemGlint, hasFoil)
            : ColoredGlintBuffers.foilDirect(buffers, baseType, itemGlint, hasFoil, color);
    }

    @Redirect(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getCompassFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")
    )
    private VertexConsumer relicWeapons$coloredCompassFoil(
        MultiBufferSource buffers, RenderType baseType, PoseStack.Pose pose
    ) {
        int color = relicWeapons$currentGlintColor();
        return color < 0
            ? ItemRenderer.getCompassFoilBuffer(buffers, baseType, pose)
            : ColoredGlintBuffers.compass(buffers, baseType, pose, color);
    }

    private static int relicWeapons$currentGlintColor() {
        Deque<Integer> colors = RELIC_WEAPONS_GLINT_COLORS.get();
        return colors.isEmpty() ? -1 : colors.peek();
    }

    @ModifyArg(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
        ),
        index = 2
    )
    private int relicWeapons$fullBrightRadiantSurface(int combinedLight) {
        Deque<Integer> radiance = RELIC_WEAPONS_RADIANCE_LEVELS.get();
        return !radiance.isEmpty() && radiance.peek() > 0
            ? LightTexture.FULL_BRIGHT
            : combinedLight;
    }
}
