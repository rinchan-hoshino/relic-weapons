package dev.rinchan.relicweapons.mixin;

import dev.rinchan.relicweapons.RelicWeapons;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.lambdaurora.lambdynlights.api.item.ItemLightSources", remap = false)
public abstract class SodiumDynamicLightsItemLightSourcesMixin {
    @Inject(method = "getLuminance", at = @At("RETURN"), cancellable = true, remap = false)
    private static void relicWeapons$getRadianceLevel(ItemStack stack, boolean submergedInWater, CallbackInfoReturnable<Integer> cir) {
        int radiance = RelicWeapons.radianceLevel(stack);
        if (radiance > cir.getReturnValue()) {
            cir.setReturnValue(radiance);
        }
    }
}
