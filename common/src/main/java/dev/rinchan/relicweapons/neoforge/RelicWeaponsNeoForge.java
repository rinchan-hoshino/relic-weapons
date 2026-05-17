package dev.rinchan.relicweapons.neoforge;

import dev.rinchan.relicweapons.RelicWeapons;
import dev.rinchan.relicweapons.registry.RelicWeaponsRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(RelicWeapons.MOD_ID)
public class RelicWeaponsNeoForge {
    public RelicWeaponsNeoForge(IEventBus modBus) {
        RelicWeaponsRegistries.register(modBus);
        if (Boolean.getBoolean("relicWeapons.screenshot")) {
            ScreenshotServerHarness.register();
            if (FMLEnvironment.dist.isClient()) {
                ScreenshotClientHarness.register();
            }
        }
    }
}
