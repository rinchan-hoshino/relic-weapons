package dev.rinchan.relicweapons.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.rinchan.relicweapons.api.VisualEffects;
import dev.rinchan.relicweapons.client.ColoredGlintBuffers;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class FabricHumanoidArmorLayerMixin {
    private static final ThreadLocal<Deque<Integer>> RELIC_WEAPONS_ARMOR_GLINT_COLORS =
        ThreadLocal.withInitial(ArrayDeque::new);

    @Inject(method = "renderArmorPiece", at = @At("HEAD"))
    private void relicWeapons$pushArmorGlintColor(
        PoseStack poseStack,
        MultiBufferSource buffers,
        LivingEntity entity,
        EquipmentSlot slot,
        int light,
        HumanoidModel<?> model,
        CallbackInfo ci
    ) {
        RELIC_WEAPONS_ARMOR_GLINT_COLORS.get().push(
            VisualEffects.glintColor(entity.getItemBySlot(slot)).orElse(-1));
    }

    @Inject(method = "renderArmorPiece", at = @At("RETURN"))
    private void relicWeapons$popArmorGlintColor(
        PoseStack poseStack,
        MultiBufferSource buffers,
        LivingEntity entity,
        EquipmentSlot slot,
        int light,
        HumanoidModel<?> model,
        CallbackInfo ci
    ) {
        Deque<Integer> colors = RELIC_WEAPONS_ARMOR_GLINT_COLORS.get();
        colors.pop();
        if (colors.isEmpty()) {
            RELIC_WEAPONS_ARMOR_GLINT_COLORS.remove();
        }
    }

    @Redirect(
        method = "renderGlint",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")
    )
    private VertexConsumer relicWeapons$coloredArmorGlint(MultiBufferSource buffers, RenderType glintType) {
        Deque<Integer> colors = RELIC_WEAPONS_ARMOR_GLINT_COLORS.get();
        int color = colors.isEmpty() ? -1 : colors.peek();
        return color < 0
            ? buffers.getBuffer(glintType)
            : ColoredGlintBuffers.armor(buffers, color);
    }
}
