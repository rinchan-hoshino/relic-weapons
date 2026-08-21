package dev.rinchan.relicweapons.client;

import dev.rinchan.relicweapons.api.ParticleAnimation;
import dev.rinchan.relicweapons.api.ParticleEffect;
import dev.rinchan.relicweapons.api.VisualEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class ParticleClientEvents {
    private static final int MAX_PARTICLES_PER_CLIENT_TICK = 512;

    private ParticleClientEvents() {
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.isPaused()) {
            return;
        }

        int budget = MAX_PARTICLES_PER_CLIENT_TICK;
        for (Entity entity : level.entitiesForRendering()) {
            if (budget <= 0) {
                break;
            }
            if (entity instanceof ItemEntity itemEntity) {
                budget -= spawnForStack(
                        level,
                        itemEntity.getItem(),
                        itemEntity.position().add(0.0, 0.15, 0.0),
                        itemEntity.getDeltaMovement(),
                        itemEntity.getDeltaMovement(),
                        budget);
            } else if (entity instanceof LivingEntity living) {
                budget -= spawnForLiving(level, living, budget);
            }
        }
    }

    private static int spawnForLiving(ClientLevel level, LivingEntity entity, int budget) {
        int spawned = 0;
        Vec3 forward = entity.getLookAngle();
        Vec3 motion = entity.getDeltaMovement();
        HumanoidArm mainArm = entity.getMainArm();
        HumanoidArm offArm = mainArm.getOpposite();
        boolean firstPersonPlayer = entity == Minecraft.getInstance().player
            && Minecraft.getInstance().options.getCameraType().isFirstPerson();
        Vec3 mainHandAnchor = firstPersonPlayer
            ? HandParticleAnchor.firstPerson(entity.getEyePosition(), forward, mainArm)
            : HandParticleAnchor.thirdPerson(entity.position(), entity.getBbHeight(), forward, entity.yBodyRot, mainArm);
        Vec3 offHandAnchor = firstPersonPlayer
            ? HandParticleAnchor.firstPerson(entity.getEyePosition(), forward, offArm)
            : HandParticleAnchor.thirdPerson(entity.position(), entity.getBbHeight(), forward, entity.yBodyRot, offArm);
        spawned += spawnForStack(level, entity.getMainHandItem(), mainHandAnchor, forward, motion, budget - spawned);
        spawned += spawnForStack(level, entity.getOffhandItem(), offHandAnchor, forward, motion, budget - spawned);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR || spawned >= budget) {
                continue;
            }
            double fraction = switch (slot) {
                case HEAD -> 0.92;
                case CHEST -> 0.68;
                case LEGS -> 0.42;
                case FEET -> 0.12;
                default -> 0.5;
            };
            Vec3 anchor = entity.position().add(0.0, entity.getBbHeight() * fraction, 0.0);
            spawned += spawnForStack(level, entity.getItemBySlot(slot), anchor, forward, motion, budget - spawned);
        }
        return spawned;
    }

    private static int spawnForStack(
            ClientLevel level,
            ItemStack stack,
            Vec3 anchor,
            Vec3 forward,
            Vec3 carrierMotion,
            int budget) {
        if (stack.isEmpty() || budget <= 0) {
            return 0;
        }
        ParticleEffect effect = VisualEffects.particle(stack).orElse(null);
        if (effect == null || effect.rate() <= 0.0F) {
            return 0;
        }

        RandomSource random = level.random;
        float expected = effect.rate() / 20.0F;
        int count = Mth.floor(expected);
        if (random.nextFloat() < expected - count) {
            count++;
        }
        count = Math.min(count, budget);

        for (int i = 0; i < count; i++) {
            Vec3 offset = sampleOffset(effect, random, forward, carrierMotion);
            Vec3 velocity = sampleDirection(effect, random, offset, forward, carrierMotion)
                    .scale(effect.speed())
                    .add(carrierMotion.scale(effect.inheritMotion()));
            Vec3 position = anchor.add(offset);
            level.addParticle(effect.particle(), position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        }
        return count;
    }

    private static Vec3 sampleOffset(
            ParticleEffect effect, RandomSource random, Vec3 forward, Vec3 carrierMotion) {
        double spread = effect.spread();
        return switch (effect.shape()) {
            case POINT -> randomUnit(random).scale(spread * random.nextDouble());
            case SPHERE -> randomUnit(random).scale(spread);
            case RING -> {
                double angle = random.nextDouble() * Mth.TWO_PI;
                yield new Vec3(Math.cos(angle) * spread, 0.0, Math.sin(angle) * spread);
            }
            case TRAIL -> {
                Vec3 axis = normalizeOr(carrierMotion, normalizeOr(forward, new Vec3(0.0, 0.0, 1.0)));
                yield axis.scale(-spread * random.nextDouble());
            }
        };
    }

    private static Vec3 sampleDirection(
            ParticleEffect effect,
            RandomSource random,
            Vec3 offset,
            Vec3 forward,
            Vec3 carrierMotion) {
        return switch (effect.direction()) {
            case RANDOM -> randomUnit(random);
            case UP -> new Vec3(0.0, 1.0, 0.0);
            case OUTWARD -> normalizeOr(offset, randomUnit(random));
            case FORWARD -> normalizeOr(forward, new Vec3(0.0, 0.0, 1.0));
            case MOTION -> normalizeOr(carrierMotion, new Vec3(0.0, 1.0, 0.0));
        };
    }

    private static Vec3 randomUnit(RandomSource random) {
        double y = random.nextDouble() * 2.0 - 1.0;
        double angle = random.nextDouble() * Mth.TWO_PI;
        double radius = Math.sqrt(Math.max(0.0, 1.0 - y * y));
        return new Vec3(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
    }

    private static Vec3 normalizeOr(Vec3 vector, Vec3 fallback) {
        return vector.lengthSqr() < 1.0E-8 ? fallback : vector.normalize();
    }
}
