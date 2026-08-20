package dev.rinchan.relicweapons.neoforge;

import dev.rinchan.relicweapons.RelicWeapons;
import dev.rinchan.relicweapons.api.ParticleAnimation;
import dev.rinchan.relicweapons.api.ParticleEffect;
import dev.rinchan.relicweapons.api.VisualEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.Difficulty;
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
        if (event.getServer().getPlayerList().getPlayers().isEmpty()) {
            return;
        }
        ServerPlayer player = event.getServer().getPlayerList().getPlayers().getFirst();
        player.setGameMode(GameType.CREATIVE);
        player.getAbilities().invulnerable = true;
        player.onUpdateAbilities();
        player.setHealth(player.getMaxHealth());
        event.getServer().setDifficulty(Difficulty.PEACEFUL, true);
        if (prepared || ticks < 20) {
            return;
        }
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
        RelicWeapons.applyEnchantmentGlow(sword, 0xFF7700);
        ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
        RelicWeapons.applyTextureLight(axe, RelicWeapons.DEFAULT_RADIANCE_LEVEL);
        VisualEffects.setParticle(axe, new ParticleEffect(
            ParticleTypes.END_ROD,
            ParticleAnimation.Shape.RING,
            12.0F,
            0.45F,
            ParticleAnimation.Direction.OUTWARD,
            0.04F,
            0.3F));
        player.setGameMode(GameType.CREATIVE);
        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        RelicWeapons.applyEnchantmentGlow(chestplate, 0x32FF66);
        player.getInventory().clearContent();
        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, axe);
        player.setItemSlot(EquipmentSlot.CHEST, chestplate);
        player.setPos(base.getX() + 0.5, base.getY() + 1, base.getZ() + 0.5);
        player.setYRot(35.0F);
        player.setXRot(15.0F);
        prepared = true;
    }
}
