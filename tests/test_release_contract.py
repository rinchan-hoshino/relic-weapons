from pathlib import Path
import json
import unittest

ROOT = Path(__file__).resolve().parents[1]


class RelicWeaponsReleaseContractTests(unittest.TestCase):
    def test_release_is_breaking_content_free_030_api(self):
        props = dict(
            line.split("=", 1)
            for line in (ROOT / "gradle.properties").read_text().splitlines()
            if "=" in line and not line.lstrip().startswith("#")
        )
        self.assertEqual("0.3.0", props["mod_version"])
        self.assertEqual("GPL-3.0-only", props["mod_license"])

        registry = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/registry/RelicWeaponsRegistries.java").read_text()
        self.assertNotIn("DeferredRegister<Item>", registry)
        self.assertNotIn("ENCHANTMENT_GLOW", registry)
        self.assertNotIn("TEXTURE_LIGHT", registry)
        for path in (ROOT / "common/src/main/resources").rglob("*"):
            if path.is_file():
                self.assertNotIn("enchantment_glow", path.as_posix())
                self.assertNotIn("texture_light", path.as_posix())

    def test_independent_components_and_public_api_are_registered(self):
        registry = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/registry/RelicWeaponsRegistries.java").read_text()
        for component in ("GLINT_COLOR", "RADIANCE_LEVEL", "PARTICLE_EFFECT"):
            self.assertIn(component, registry)
        neo_registries = (ROOT / "neoforge/src/main/java/dev/rinchan/relicweapons/neoforge/RelicWeaponsNeoForgeRegistries.java").read_text()
        fabric_entrypoint = (ROOT / "fabric/src/main/java/dev/rinchan/relicweapons/fabric/RelicWeaponsFabric.java").read_text()
        for component in ("glint_color", "radiance_level", "particle_effect"):
            self.assertIn(f'"{component}"', neo_registries)
            self.assertIn(f'"{component}"', fabric_entrypoint)
        api = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/api/VisualEffects.java").read_text()
        self.assertIn("ENCHANTMENT_GLINT_OVERRIDE", api)
        self.assertNotIn("DataComponents.ENCHANTMENTS", api)
        self.assertIn("setParticle", api)
        self.assertIn("copy(ItemStack source, ItemStack target)", api)

    def test_colored_glint_is_consumed_by_item_and_armor_renderers(self):
        mixins = json.loads((ROOT / "common/src/main/resources/relic_weapons.mixins.json").read_text())
        self.assertTrue(mixins["required"])
        self.assertEqual(1, mixins["injectors"]["defaultRequire"])
        self.assertIn("ItemRendererMixin", mixins["client"])
        item = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/mixin/ItemRendererMixin.java").read_text()
        neo_armor = (ROOT / "neoforge/src/main/java/dev/rinchan/relicweapons/mixin/HumanoidArmorLayerMixin.java").read_text()
        fabric_armor = (ROOT / "fabric/src/main/java/dev/rinchan/relicweapons/fabric/mixin/FabricHumanoidArmorLayerMixin.java").read_text()
        self.assertIn("VisualEffects.glintColor", item)
        self.assertIn("ColoredGlintBuffers", item)
        for armor in (neo_armor, fabric_armor):
            self.assertIn("VisualEffects.glintColor", armor)
            self.assertIn("ColoredGlintBuffers.armor", armor)
        render_types = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/client/ColoredGlintRenderTypes.java").read_text()
        buffers = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/client/ColoredGlintBuffers.java").read_text()
        vertex_consumer = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/client/ColoredVertexConsumer.java").read_text()
        fragment = (ROOT / "common/src/main/resources/assets/relic_weapons/shaders/core/colored_glint.fsh").read_text()
        vertex = (ROOT / "common/src/main/resources/assets/relic_weapons/shaders/core/colored_glint.vsh").read_text()
        self.assertIn("RelicShaders::coloredGlint", render_types)
        self.assertIn("POSITION_TEX_COLOR", render_types)
        self.assertIn("computeIfAbsent", buffers)
        self.assertIn("delegate.setColor(red, green, blue, alpha)", vertex_consumer)
        self.assertIn("vertexColor = Color", vertex)
        self.assertIn("vertexColor.rgb * ColorModulator.rgb * intensity", fragment)

    def test_fabric_and_neoforge_load_the_same_public_effect_implementation(self):
        settings = (ROOT / "settings.gradle").read_text()
        self.assertIn("include 'fabric'", settings)
        self.assertIn("include 'neoforge'", settings)
        fabric_metadata = json.loads((ROOT / "fabric/src/main/resources/fabric.mod.json").read_text())
        self.assertEqual("${mod_id}", fabric_metadata["id"])
        self.assertIn("relic_weapons.mixins.json", fabric_metadata["mixins"])
        fabric_client = (ROOT / "fabric/src/main/java/dev/rinchan/relicweapons/fabric/RelicWeaponsFabricClient.java").read_text()
        self.assertIn("ParticleClientEvents.tick()", fabric_client)
        self.assertIn("RelicShaders::setColoredGlint", fabric_client)

    def test_particle_contract_supports_registry_dispatched_options_and_bounded_motion(self):
        effect = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/api/ParticleEffect.java").read_text()
        animation = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/api/ParticleAnimation.java").read_text()
        client = (ROOT / "common/src/main/java/dev/rinchan/relicweapons/client/ParticleClientEvents.java").read_text()
        self.assertIn("ParticleTypes.CODEC", effect)
        self.assertIn("MAX_RATE = 64.0F", animation)
        self.assertIn("MAX_PARTICLES_PER_CLIENT_TICK = 512", client)
        for shape in ("POINT", "SPHERE", "RING", "TRAIL"):
            self.assertIn(shape, animation)
        for direction in ("RANDOM", "UP", "OUTWARD", "FORWARD", "MOTION"):
            self.assertIn(direction, animation)


if __name__ == "__main__":
    unittest.main()
