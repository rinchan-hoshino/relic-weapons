package dev.rinchan.luminousgear.neoforge;

import dev.rinchan.luminousgear.LuminousGear;
import dev.rinchan.luminousgear.registry.LuminousGearRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(LuminousGear.MOD_ID)
public class LuminousGearNeoForge {
    public LuminousGearNeoForge(IEventBus modBus) {
        LuminousGearRegistries.register(modBus);
        if (Boolean.getBoolean("luminousGear.screenshot")) {
            ScreenshotServerHarness.register();
            if (FMLEnvironment.dist.isClient()) {
                ScreenshotClientHarness.register();
            }
        }
    }
}
