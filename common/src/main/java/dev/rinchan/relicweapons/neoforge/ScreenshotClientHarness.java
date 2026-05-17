package dev.rinchan.relicweapons.neoforge;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.tutorial.TutorialSteps;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

final class ScreenshotClientHarness {
    private static int clientTicks;
    private static int inWorldTicks;
    private static boolean connecting;
    private static boolean firstCaptured;
    private static boolean secondCaptured;
    private static File directory;

    private ScreenshotClientHarness() {}

    public static void register() {
        directory = new File(System.getProperty("relicWeapons.screenshot.dir", "screenshots"));
        directory.mkdirs();
        NeoForge.EVENT_BUS.addListener(ScreenshotClientHarness::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        clientTicks++;
        if (!connecting && minecraft.player == null && minecraft.level == null && clientTicks >= 120) {
            connecting = true;
            if (minecraft.screen == null) {
                minecraft.setScreen(new TitleScreen());
            }
            ConnectScreen.startConnecting(
                minecraft.screen,
                minecraft,
                ServerAddress.parseString("localhost"),
                new ServerData("Relic Weapons Screenshot", "localhost", ServerData.Type.OTHER),
                false,
                null
            );
        }
        if (minecraft.player == null || minecraft.level == null || minecraft.getMainRenderTarget() == null) {
            return;
        }
        inWorldTicks++;
        minecraft.options.tutorialStep = TutorialSteps.NONE;
        minecraft.options.hideGui = false;
        minecraft.options.pauseOnLostFocus = false;
        minecraft.options.setCameraType(CameraType.FIRST_PERSON);
        minecraft.gui.getChat().clearMessages(false);
        minecraft.getToasts().clear();
        minecraft.player.setYRot(35.0F);
        minecraft.player.setXRot(18.0F);
        minecraft.player.getInventory().selected = 0;

        if (!firstCaptured && inWorldTicks >= 180) {
            save(minecraft, "relic-weapons-held-items.png");
            firstCaptured = true;
        }
        if (!secondCaptured && inWorldTicks >= 220) {
            minecraft.setScreen(new InventoryScreen(minecraft.player));
        }
        if (!secondCaptured && inWorldTicks >= 260) {
            save(minecraft, "relic-weapons-inventory.png");
            secondCaptured = true;
        }
        if ((secondCaptured && inWorldTicks >= 300) || inWorldTicks >= 700) {
            minecraft.stop();
        }
    }

    private static void save(Minecraft minecraft, String name) {
        try (NativeImage image = Screenshot.takeScreenshot(minecraft.getMainRenderTarget())) {
            image.writeToFile(new File(directory, name));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save screenshot " + name, e);
        }
    }
}
