package dev.rinchan.relicweapons.neoforge;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.rinchan.relicweapons.RelicWeapons;
import dev.rinchan.relicweapons.client.ParticleClientEvents;
import dev.rinchan.relicweapons.client.RelicShaders;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(RelicWeapons.MOD_ID)
public class RelicWeaponsNeoForge {
    public RelicWeaponsNeoForge(IEventBus modBus) {
        RelicWeaponsNeoForgeRegistries.register(modBus);
        if (FMLEnvironment.dist.isClient()) {
            modBus.addListener(RelicWeaponsNeoForge::registerShaders);
            NeoForge.EVENT_BUS.addListener(RelicWeaponsNeoForge::onClientTick);
        }
        if (Boolean.getBoolean("relicWeapons.screenshot")) {
            ScreenshotServerHarness.register();
            if (FMLEnvironment.dist.isClient()) {
                ScreenshotClientHarness.register();
            }
        }
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        ParticleClientEvents.tick();
    }

    private static void registerShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(RelicWeapons.MOD_ID, "colored_glint"),
                    DefaultVertexFormat.POSITION_TEX_COLOR),
                RelicShaders::setColoredGlint);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to register the Relic Weapons colored-glint shader", exception);
        }
    }
}
