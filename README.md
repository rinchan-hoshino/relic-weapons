# Relic Weapons

**A data-driven visual-effects API for any item.**

Relic Weapons 0.3 provides three independent item-stack effects without registering any catalyst item or default recipe:

- **Colored enchantment glint** uses `minecraft:enchantment_glint_override` plus `relic_weapons:glint_color`. It does not add an enchantment or participate in enchantment rules.
- **Radiance** stores `relic_weapons:radiance_level`, renders visible item surfaces at full brightness, and exposes the configured light level to Sodium Dynamic Lights.
- **Particles** store a registry-dispatched vanilla or modded particle option in `relic_weapons:particle_effect` together with bounded animation parameters.

Pack authors decide how effects are acquired. The mod ships no items and no active recipes.

## Java API

```java
VisualEffects.setGlint(stack, 0xFF7700);
VisualEffects.setRadiance(stack, 2);
VisualEffects.setParticle(stack, new ParticleEffect(
    ParticleTypes.END_ROD,
    ParticleAnimation.Shape.RING,
    12.0F,  // particles per second
    0.45F,  // spread in blocks
    ParticleAnimation.Direction.OUTWARD,
    0.04F,  // particle speed
    0.30F   // inherited carrier motion
));
```

`VisualEffects.copy(source, target)` copies the complete visual profile without copying unrelated item components. `VisualEffects.clear(stack)` removes only Relic Weapons effects and the explicit glint override.

## Datapack smithing API

The retained `relic_weapons:glow_smithing` recipe type lets a datapack use any external item as a catalyst.

Colored glint:

```json
{
  "type": "relic_weapons:glow_smithing",
  "template": { "item": "minecraft:amethyst_shard" },
  "glow_type": "enchantment",
  "color": 16742144
}
```

Arbitrary particle options and animation:

```json
{
  "type": "relic_weapons:glow_smithing",
  "template": { "item": "minecraft:blaze_powder" },
  "glow_type": "particle",
  "particle": {
    "type": "minecraft:flame"
  },
  "shape": "trail",
  "rate": 10.0,
  "spread": 0.5,
  "direction": "forward",
  "speed": 0.08,
  "inherit_motion": 0.4
}
```

Supported shapes are `point`, `sphere`, `ring`, and `trail`. Supported directions are `random`, `up`, `outward`, `forward`, and `motion`. Rates and motion values are bounded in the component codec, and the client also enforces a global per-tick particle budget.

Particles render while an affected item is held, worn as humanoid armor, or present as an item entity. Per-item glint tinting covers vanilla item models and humanoid armor; custom item renderers must opt into the API or vanilla glint buffers themselves.

## 0.3 migration

Version 0.3 removes the old `relic_weapons:enchantment_glow` and `relic_weapons:texture_light` items and their bundled recipes. Existing visual-effect components and the smithing recipe serializer remain available. Packs that used the removed catalyst items must provide their own acquisition item or call the Java API.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1 or newer, or Fabric Loader 0.19.2 or newer with Fabric API

Both loader artifacts are built from the same visual-profile, particle, recipe, shader, and renderer implementation:

```bash
./gradlew :neoforge:build :fabric:build
```
