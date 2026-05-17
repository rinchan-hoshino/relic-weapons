package dev.rinchan.luminousgear.neoforge;

import dev.rinchan.luminousgear.LuminousGear;
import dev.rinchan.luminousgear.registry.LuminousGearRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

final class ScreenshotServerHarness {
    private static int ticks;
    private static boolean prepared;

    private ScreenshotServerHarness() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ScreenshotServerHarness::onServerTick);
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        ticks++;
        if (prepared || ticks < 80 || event.getServer().getPlayerList().getPlayers().isEmpty()) {
            return;
        }
        ServerPlayer player = event.getServer().getPlayerList().getPlayers().getFirst();
        ServerLevel level = player.serverLevel();
        level.setDayTime(14000);
        BlockPos base = BlockPos.containing(player.getX(), player.getY() - 1, player.getZ());
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                level.setBlock(base.offset(x, 0, z), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 3);
                level.setBlock(base.offset(x, 1, z), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(base.offset(x, 2, z), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        level.setBlock(base.offset(1, 1, 0), Blocks.SMITHING_TABLE.defaultBlockState(), 3);
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        LuminousGear.applyEnchantmentGlow(sword, LuminousGear.VANILLA_GLINT_COLOR);
        ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
        LuminousGear.applyTextureLight(axe, LuminousGear.DEFAULT_RADIANCE_LEVEL);
        player.setGameMode(GameType.SURVIVAL);
        player.getInventory().clearContent();
        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, axe);
        player.getInventory().setItem(2, new ItemStack(LuminousGearRegistries.ENCHANTMENT_GLOW.get(), 4));
        player.getInventory().setItem(3, new ItemStack(LuminousGearRegistries.TEXTURE_LIGHT.get(), 4));
        player.setPos(base.getX() + 0.5, base.getY() + 1, base.getZ() + 0.5);
        player.setYRot(35.0F);
        player.setXRot(15.0F);
        prepared = true;
    }
}
