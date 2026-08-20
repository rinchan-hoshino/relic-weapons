package dev.rinchan.relicweapons.mixin;

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
public abstract class HumanoidArmorLayerMixin {
    private static final String RENDER_ARMOR_PIECE =
        "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;"
            + "Lnet/minecraft/client/renderer/MultiBufferSource;"
            + "Lnet/minecraft/world/entity/LivingEntity;"
            + "Lnet/minecraft/world/entity/EquipmentSlot;I"
            + "Lnet/minecraft/client/model/HumanoidModel;FFFFFF)V";
    private static final String RENDER_GLINT =
        "renderGlint(Lcom/mojang/blaze3d/vertex/PoseStack;"
            + "Lnet/minecraft/client/renderer/MultiBufferSource;I"
            + "Lnet/minecraft/client/model/Model;)V";
    private static final ThreadLocal<Deque<Integer>> RELIC_WEAPONS_ARMOR_GLINT_COLORS =
        ThreadLocal.withInitial(ArrayDeque::new);

    @Inject(method = RENDER_ARMOR_PIECE, at = @At("HEAD"))
    private void relicWeapons$pushArmorGlintColor(
        PoseStack poseStack,
        MultiBufferSource buffers,
        LivingEntity entity,
        EquipmentSlot slot,
        int light,
        HumanoidModel<?> model,
        float limbSwing,
        float limbSwingAmount,
        float partialTick,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        RELIC_WEAPONS_ARMOR_GLINT_COLORS.get().push(
            VisualEffects.glintColor(entity.getItemBySlot(slot)).orElse(-1));
    }

    @Inject(method = RENDER_ARMOR_PIECE, at = @At("RETURN"))
    private void relicWeapons$popArmorGlintColor(
        PoseStack poseStack,
        MultiBufferSource buffers,
        LivingEntity entity,
        EquipmentSlot slot,
        int light,
        HumanoidModel<?> model,
        float limbSwing,
        float limbSwingAmount,
        float partialTick,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        Deque<Integer> colors = RELIC_WEAPONS_ARMOR_GLINT_COLORS.get();
        colors.pop();
        if (colors.isEmpty()) {
            RELIC_WEAPONS_ARMOR_GLINT_COLORS.remove();
        }
    }

    @Redirect(
        method = RENDER_GLINT,
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
