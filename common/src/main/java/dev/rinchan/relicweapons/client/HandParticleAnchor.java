package dev.rinchan.relicweapons.client;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;

final class HandParticleAnchor {
    private static final Vec3 WORLD_UP = new Vec3(0.0, 1.0, 0.0);

    private HandParticleAnchor() {
    }

    static Vec3 thirdPerson(Vec3 position, double boundingBoxHeight, Vec3 look, float bodyYaw, HumanoidArm arm) {
        Vec3 view = normalizeOr(look, new Vec3(0.0, 0.0, 1.0));
        Vec3 bodyForward = Vec3.directionFromRotation(0.0F, bodyYaw);
        Vec3 right = normalizeOr(bodyForward.cross(WORLD_UP), new Vec3(-1.0, 0.0, 0.0));
        return position
            .add(0.0, boundingBoxHeight * 0.72, 0.0)
            .add(view.scale(0.32))
            .add(right.scale(side(arm) * 0.30));
    }

    static Vec3 firstPerson(Vec3 eyePosition, Vec3 look, HumanoidArm arm) {
        Vec3 view = normalizeOr(look, new Vec3(0.0, 0.0, 1.0));
        Vec3 right = normalizeOr(view.cross(WORLD_UP), new Vec3(-1.0, 0.0, 0.0));
        Vec3 up = normalizeOr(right.cross(view), WORLD_UP);
        return eyePosition
            .add(view.scale(0.55))
            .add(right.scale(side(arm) * 0.28))
            .add(up.scale(-0.22));
    }

    private static double side(HumanoidArm arm) {
        return arm == HumanoidArm.RIGHT ? 1.0 : -1.0;
    }

    private static Vec3 normalizeOr(Vec3 value, Vec3 fallback) {
        return value.lengthSqr() < 1.0E-8 ? fallback : value.normalize();
    }
}
