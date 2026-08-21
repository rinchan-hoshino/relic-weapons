package dev.rinchan.relicweapons.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

class HandParticleAnchorTest {
    private static final double EPSILON = 1.0E-9;

    @Test
    void thirdPersonHandsAreMirroredAroundTheHeldItemCenterline() {
        Vec3 position = new Vec3(10.0, 20.0, 30.0);
        Vec3 look = new Vec3(0.0, 0.0, 1.0);

        Vec3 right = HandParticleAnchor.thirdPerson(position, 2.0, look, 0.0F, HumanoidArm.RIGHT);
        Vec3 left = HandParticleAnchor.thirdPerson(position, 2.0, look, 0.0F, HumanoidArm.LEFT);

        assertTrue(right.x < position.x);
        assertTrue(left.x > position.x);
        assertEquals(position.x * 2.0, right.x + left.x, EPSILON);
        assertTrue(right.z > position.z);
    }

    @Test
    void firstPersonAnchorIsInFrontOfAndBelowTheEyeAtTheSelectedHand() {
        Vec3 eye = new Vec3(4.0, 8.0, 12.0);
        Vec3 look = new Vec3(0.0, 0.0, 1.0);

        Vec3 right = HandParticleAnchor.firstPerson(eye, look, HumanoidArm.RIGHT);
        Vec3 left = HandParticleAnchor.firstPerson(eye, look, HumanoidArm.LEFT);

        assertTrue(right.x < eye.x);
        assertTrue(left.x > eye.x);
        assertTrue(right.y < eye.y);
        assertTrue(right.z > eye.z);
        assertEquals(eye.x * 2.0, right.x + left.x, EPSILON);
    }
}
