# Relic Weapons

**Forge ancient-looking relic effects onto any item.**

Relic Weapons adds two smithing materials:

- **Enchantment Glow**: adds vanilla enchantment glint to the target item. The material item itself also glints.
- **Texture Light**: marks the target item with a configurable radiance level. The default recipe stores light level 2 for dynamic-light integrations.

The mod uses custom smithing recipes that copy the base item, so pack authors can add more glow recipes through datapacks.

## Default smithing recipes

Put the glow material in the smithing template slot and any target item in the base slot. The addition slot is empty for the default recipes.

```json
{
  "type": "relic_weapons:glow_smithing",
  "template": { "item": "relic_weapons:enchantment_glow" },
  "glow_type": "enchantment"
}
```

```json
{
  "type": "relic_weapons:glow_smithing",
  "template": { "item": "relic_weapons:texture_light" },
  "glow_type": "texture_light",
  "light_level": 2
}
```

## Colored glint datapack recipes

Datapacks can add a glint color value:

```json
{
  "type": "relic_weapons:glow_smithing",
  "template": { "item": "relic_weapons:enchantment_glow" },
  "glow_type": "enchantment",
  "color": 16755200
}
```

The vanilla default glint recipe omits `color`.

## Dynamic light and shader integration

The Texture Light material is registered as a level-2 item light for Sodium Dynamic Lights. Relic Weapons also exposes the same radiance level through an item data component on crafted targets, with a client-side Sodium Dynamic Lights hook so held relic items can emit their configured light.

Vanilla Minecraft does not provide a general per-item surface-emissive material system for arbitrary existing item textures. Relic Weapons stores radiance data for dynamic-light/shader integrations and keeps the vanilla path clean instead of changing every item texture globally.

## Requirements

- Minecraft 1.21.1
- NeoForge
