package dev.rinchan.relicweapons.fabric;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.rinchan.relicweapons.RelicWeapons;
import dev.rinchan.relicweapons.client.ParticleClientEvents;
import dev.rinchan.relicweapons.client.RelicShaders;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.resources.ResourceLocation;

public final class RelicWeaponsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> ParticleClientEvents.tick());
        CoreShaderRegistrationCallback.EVENT.register(context -> context.register(
            ResourceLocation.fromNamespaceAndPath(RelicWeapons.MOD_ID, "colored_glint"),
            DefaultVertexFormat.POSITION_TEX_COLOR,
            RelicShaders::setColoredGlint));
    }
}
